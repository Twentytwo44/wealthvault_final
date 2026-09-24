package com.wealthvault.data.dashboard.repository

import com.wealthvault.core.architecture.AppError
import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.architecture.CacheFreshness
import com.wealthvault.core.cache.DashboardCache
import com.wealthvault.core.cache.DashboardCacheEntry
import com.wealthvault.core.model.DashboardData
import com.wealthvault.core.model.DashboardItem
import com.wealthvault.core.model.Money
import com.wealthvault.data.dashboard.repository.DashboardDataSource
import com.wealthvault.data.dashboard.repository.DashboardRemoteDataSource
import com.wealthvault.data.dashboard.repository.DashboardRepositoryImpl
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import com.wealthvault.domain.portfolio.DashboardSnapshot
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.time.Clock

class DashboardRepositoryTest {
    private val json = Json

    @Test
    fun freshCacheAvoidsDuplicateNetworkRequest() = runTest {
        val remote = FakeRemote(AppResult.Success(sampleDashboard()))
        val cache = MemoryCache()
        val repository = DashboardRepositoryImpl(remote, cache, json, TestLogger)

        assertIs<AppResult.Success<*>>(repository.getDashboardData(forceRefresh = true))
        assertIs<AppResult.Success<*>>(repository.getDashboardData(forceRefresh = false))

        assertEquals(1, remote.calls)
    }

    @Test
    fun staleCacheIsReturnedWhenNetworkFails() = runTest {
        val value = sampleDashboard()
        val cache = MemoryCache().apply {
            entry = DashboardCacheEntry(
                payload = json.encodeToString(value),
                updatedAtEpochMillis = Clock.System.now().toEpochMilliseconds() - (2 * 60 * 1_000L + 1),
            )
        }
        val remote = FakeRemote(AppResult.Failure(AppError.Unknown(IllegalStateException("offline"))))
        val repository = DashboardRepositoryImpl(remote, cache, json, TestLogger)

        val result = assertIs<AppResult.Success<*>>(repository.getDashboardData())
        assertEquals(CacheFreshness.Offline, (result.value as DashboardSnapshot).freshness)
    }

    @Test
    fun legacyNumericCacheIsMigratedForOfflineUse() = runTest {
        val cache = MemoryCache().apply {
            entry = DashboardCacheEntry(
                payload = """
                    {"assets":[{"id":"legacy-1","name":"Cash","value":123.45}],
                     "friendCount":1,"liabilities":[],
                     "netWorth":{"count":1,"totalAssets":123.45,"totalLiabilities":0.0,"value":123.45},
                     "uniqueSharedItemCount":0}
                """.trimIndent(),
                updatedAtEpochMillis = Clock.System.now().toEpochMilliseconds(),
            )
        }
        val remote = FakeRemote(AppResult.Failure(AppError.Unknown(IllegalStateException("offline"))))
        val repository = DashboardRepositoryImpl(remote, cache, json, TestLogger)

        val result = assertIs<AppResult.Success<*>>(repository.getDashboardData())
        val snapshot = result.value as DashboardSnapshot
        assertEquals(CacheFreshness.Fresh, snapshot.freshness)
        assertEquals(12345, snapshot.value.assets.single().value?.minorUnits)
    }

    @Test
    fun dashboardTransportMapsWireValuesBeforeReturningDomainData() = runTest {
        val client = HttpClient(MockEngine) {
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
            engine {
                addHandler {
                    respond(
                        content = """
                            {"assets":[{"id":"cash-1","type":"CASH","name":"Wallet","amount":123.45}],"friend_count":2,"liabilities":[],"net_worth":{"count":1,"total_assets":123.45,"total_liabilities":0.0,"value":123.45},"unique_shared_item_count":1}
                        """.trimIndent(),
                        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                    )
                }
            }
        }

        val result = assertIs<AppResult.Success<DashboardData>>(
            DashboardDataSource(client).fetchDashboardData(),
        )

        assertEquals(2, result.value.friendCount)
        assertEquals(12345, result.value.assets.single().value?.minorUnits)
        assertEquals(12345, result.value.netWorth?.value?.minorUnits)
        client.close()
    }

    private fun sampleDashboard() = DashboardData(
        assets = listOf(DashboardItem(id = "asset-1", name = "Cash", value = Money.fromDecimal("100.00"))),
        friendCount = 2,
    )

    private class FakeRemote(private val result: AppResult<DashboardData>) : DashboardRemoteDataSource {
        var calls: Int = 0

        override suspend fun fetchDashboardData(): AppResult<DashboardData> {
            calls += 1
            return result
        }
    }

    private class MemoryCache : DashboardCache {
        var entry: DashboardCacheEntry? = null

        override suspend fun read(): DashboardCacheEntry? = entry
        override suspend fun write(payload: String, updatedAtEpochMillis: Long) {
            entry = DashboardCacheEntry(payload, updatedAtEpochMillis)
        }
        override suspend fun clear() {
            entry = null
        }
    }

    private object TestLogger : com.wealthvault.core.observability.AppLogger {
        override fun debug(message: String) = Unit
        override fun info(message: String) = Unit
        override fun warn(message: String, cause: Throwable?) = Unit
        override fun error(message: String, cause: Throwable?) = Unit
    }
}
