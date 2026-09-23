package com.wealthvault.dashboard.data

import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.architecture.CacheFreshness
import com.wealthvault.core.architecture.CachedValue
import com.wealthvault.core.architecture.toThrowable
import com.wealthvault.core.cache.DashboardCache
import com.wealthvault.core.cache.isCacheFresh
import com.wealthvault.core.cache.sanitizeCacheTimestamp
import com.wealthvault.core.concurrency.SingleFlight
import com.wealthvault.core.observability.AppLogger
import com.wealthvault.core.observability.NoOpPerformanceTracer
import com.wealthvault.core.observability.PerformanceTracer
import com.wealthvault.core.model.DashboardData
import com.wealthvault.domain.portfolio.DashboardRepository
import com.wealthvault.domain.portfolio.DashboardSnapshot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.serialization.json.Json
import kotlinx.serialization.Serializable

class DashboardRepositoryImpl(
    private val networkDataSource: DashboardRemoteDataSource,
    private val cache: DashboardCache,
    private val json: Json,
    private val logger: AppLogger,
    private val tracer: PerformanceTracer = NoOpPerformanceTracer,
) : DashboardRepository {
    private val observed = MutableStateFlow<CachedValue<DashboardData>?>(null)
    private val refreshes = SingleFlight<String>()

    override fun observe(): Flow<CachedValue<DashboardData>> = observed
        .filterNotNull()
        .distinctUntilChanged()

    override suspend fun refresh(force: Boolean): AppResult<Unit> {
        val result = getDashboardData(force)
        return when (result) {
            is AppResult.Success -> AppResult.Success(Unit)
            is AppResult.Failure -> AppResult.Failure(result.error)
        }
    }

    override suspend fun getDashboardData(forceRefresh: Boolean): AppResult<DashboardSnapshot> {
        return refreshes.execute(DASHBOARD_KEY) {
            getDashboardDataInternal(forceRefresh)
        }
    }

    private suspend fun getDashboardDataInternal(forceRefresh: Boolean): AppResult<DashboardSnapshot> {
        val now = kotlin.time.Clock.System.now().toEpochMilliseconds()
        val cached = try {
            cache.read()?.let { entry ->
                runCatching { decodeDashboard(entry.payload) }
                    .onFailure { error ->
                        if (error is kotlinx.coroutines.CancellationException) throw error
                        logger.warn("Dashboard cache decode failed", error)
                    }
                    .getOrNull()
                    ?.let { value ->
                        val updatedAt = sanitizeCacheTimestamp(
                            entry.updatedAtEpochMillis,
                            now,
                            DASHBOARD_TTL_MILLIS,
                        )
                        CachedDashboard(value, updatedAt)
                    }
            }
        } catch (error: Throwable) {
            if (error is kotlinx.coroutines.CancellationException) throw error
            logger.warn("Dashboard cache read failed", error)
            null
        }

        if (!forceRefresh && cached != null && isCacheFresh(cached.updatedAtEpochMillis, now, DASHBOARD_TTL_MILLIS)) {
            logger.debug("Dashboard served from fresh cache")
            val snapshot = DashboardSnapshot(cached.value, CacheFreshness.Fresh)
            observed.value = CachedValue(snapshot.value, snapshot.freshness, cached.updatedAtEpochMillis)
            return AppResult.Success(snapshot)
        }

        // Stale-while-revalidate: publish usable cached content immediately
        // while the refresh is in flight instead of leaving the screen empty.
        cached?.let {
            observed.value = CachedValue(
                value = it.value,
                freshness = CacheFreshness.Stale,
                updatedAtEpochMillis = it.updatedAtEpochMillis,
            )
        }

        val networkResult = tracer.trace("dashboard.network.refresh") {
            networkDataSource.fetchDashboardData()
        }
        return when (networkResult) {
            is AppResult.Success -> {
                runCatching {
                    cache.write(json.encodeToString(networkResult.value), now)
                }.onFailure { error ->
                    if (error is kotlinx.coroutines.CancellationException) throw error
                    logger.warn("Dashboard cache write failed", error)
                }
                logger.info("Dashboard refreshed")
                observed.value = CachedValue(networkResult.value, CacheFreshness.Fresh, now)
                AppResult.Success(DashboardSnapshot(networkResult.value, CacheFreshness.Fresh))
            }

            is AppResult.Failure -> {
                logger.warn("Dashboard refresh failed", networkResult.error.toThrowable())
                cached?.let {
                    observed.value = CachedValue(it.value, CacheFreshness.Offline, it.updatedAtEpochMillis)
                    AppResult.Success(DashboardSnapshot(it.value, CacheFreshness.Offline))
                } ?: AppResult.Failure(networkResult.error)
            }
        }
    }

    private data class CachedDashboard(
        val value: DashboardData,
        val updatedAtEpochMillis: Long,
    )

    /**
     * Dashboard cache entries written before Money was introduced stored
     * numeric values directly. Decode those entries once and normalize them
     * into the fixed-point domain model instead of dropping offline content.
     */
    private fun decodeDashboard(payload: String): DashboardData {
        return runCatching { json.decodeFromString<DashboardData>(payload) }
            .getOrElse {
                json.decodeFromString<LegacyDashboardData>(payload).toDomain()
            }
    }

    private companion object {
        const val DASHBOARD_KEY = "dashboard"
        const val DASHBOARD_TTL_MILLIS = 2 * 60 * 1_000L
    }
}

@Serializable
internal data class LegacyDashboardData(
    val assets: List<LegacyDashboardItem> = emptyList(),
    val friendCount: Int = 0,
    val liabilities: List<LegacyDashboardItem> = emptyList(),
    val netWorth: LegacyDashboardNetWorth? = null,
    val uniqueSharedItemCount: Int = 0,
)

@Serializable
internal data class LegacyDashboardItem(
    val id: String = "",
    val type: String = "",
    val name: String = "",
    val value: Double? = null,
    val createdAt: String? = null,
)

@Serializable
internal data class LegacyDashboardNetWorth(
    val count: Int = 0,
    val totalAssets: Double = 0.0,
    val totalLiabilities: Double = 0.0,
    val value: Double = 0.0,
)

private fun LegacyDashboardData.toDomain() = DashboardData(
    assets = assets.map { it.toDomain() },
    friendCount = friendCount,
    liabilities = liabilities.map { it.toDomain() },
    netWorth = netWorth?.let {
        com.wealthvault.core.model.DashboardNetWorth(
            count = it.count,
            totalAssets = com.wealthvault.core.model.Money.fromDouble(it.totalAssets)
                ?: com.wealthvault.core.model.Money(0),
            totalLiabilities = com.wealthvault.core.model.Money.fromDouble(it.totalLiabilities)
                ?: com.wealthvault.core.model.Money(0),
            value = com.wealthvault.core.model.Money.fromDouble(it.value)
                ?: com.wealthvault.core.model.Money(0),
        )
    },
    uniqueSharedItemCount = uniqueSharedItemCount,
)

private fun LegacyDashboardItem.toDomain() = com.wealthvault.core.model.DashboardItem(
    id = id,
    type = type,
    name = name,
    value = com.wealthvault.core.model.Money.fromDouble(value),
    createdAt = createdAt,
)
