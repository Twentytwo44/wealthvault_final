package com.wealthvault.data.notification

import com.wealthvault.core.architecture.AppError
import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.architecture.CacheFreshness
import com.wealthvault.core.cache.NotificationCache
import com.wealthvault.core.cache.NotificationCacheEntry
import com.wealthvault.core.model.NotificationItem
import com.wealthvault.domain.notification.NotificationSnapshot
import com.wealthvault.core.KoinConst
import com.wealthvault.core.notification.NotificationBadgeProvider
import com.wealthvault.core.observability.AppLogger
import com.wealthvault.core.observability.NoOpAppLogger
import com.wealthvault.core.observability.NoOpPerformanceTracer
import com.wealthvault.core.observability.PerformanceTracer
import com.wealthvault.domain.notification.NotificationMutationRepository
import com.wealthvault.domain.notification.NotificationRepository
import com.wealthvault.data.notification.transport.notification.GetNotificationsApi
import com.wealthvault.data.notification.transport.read.PutNotiApi
import com.wealthvault.data.notification.transport.readall.PutNotiReadAllApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.core.qualifier.named
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.time.Clock

class NotificationRepositoryTest {
    private val json = Json

    @Test
    fun freshCacheAvoidsDuplicateNetworkRequest() = runTest {
        val remote = FakeRemote(AppResult.Success(listOf(sampleNotification())))
        val cache = MemoryCache()
        val repository = NotificationRepositoryImpl(remote, cache, json, TestLogger)

        assertIs<AppResult.Success<*>>(repository.getNoti(forceRefresh = true))
        assertIs<AppResult.Success<*>>(repository.getNoti(forceRefresh = false))

        assertEquals(1, remote.calls)
    }

    @Test
    fun staleCacheIsReturnedWhenNetworkFails() = runTest {
        val cache = MemoryCache().apply {
            entry = NotificationCacheEntry(
                payload = json.encodeToString(listOf(sampleNotification())),
                updatedAtEpochMillis = Clock.System.now().toEpochMilliseconds() - (30 * 1_000L + 1),
            )
        }
        val remote = FakeRemote(AppResult.Failure(AppError.Unknown(IllegalStateException("offline"))))
        val repository = NotificationRepositoryImpl(remote, cache, json, TestLogger)

        val result = assertIs<AppResult.Success<*>>(repository.getNoti())
        assertEquals(CacheFreshness.Offline, (result.value as NotificationSnapshot).freshness)
    }

    @Test
    fun invalidationClearsPersistedSnapshot() = runTest {
        val remote = FakeRemote(AppResult.Success(listOf(sampleNotification())))
        val cache = MemoryCache()
        val repository = NotificationRepositoryImpl(remote, cache, json, TestLogger)

        repository.getNoti(forceRefresh = true)
        repository.invalidate()

        assertEquals(null, cache.entry)
    }

    @Test
    fun invalidCacheFallsBackToNetworkAndRefreshReportsFailure() = runTest {
        val cache = MemoryCache().apply {
            entry = NotificationCacheEntry("not-json", Clock.System.now().toEpochMilliseconds())
        }
        val remote = FakeRemote(AppResult.Success(listOf(sampleNotification())))
        val repository = NotificationRepositoryImpl(remote, cache, json, TestLogger)

        val success = assertIs<AppResult.Success<NotificationSnapshot>>(repository.getNoti())
        assertEquals(CacheFreshness.Fresh, success.value.freshness)
        assertEquals(1, remote.calls)

        val failing = NotificationRepositoryImpl(
            FakeRemote(AppResult.Failure(AppError.Unknown(IllegalStateException("offline")))),
            MemoryCache(),
            json,
            TestLogger,
        )
        assertIs<AppResult.Failure>(failing.refresh(force = true))
    }

    @Test
    fun mutationRepositoriesInvalidateCacheAndBadgeReflectsUnreadState() = runTest {
        val fakes = NotificationFakes()
        val cache = MemoryCache()
        val application = koinApplication {
            modules(module {
                single<GetNotificationsApi> { fakes }
                single<PutNotiApi> { fakes }
                single<PutNotiReadAllApi> { fakes }
                single<NotificationCache> { cache }
                single<AppLogger> { NoOpAppLogger }
                single<PerformanceTracer> { NoOpPerformanceTracer }
                single<kotlinx.serialization.json.Json>(named(KoinConst.KotlinSerialization.GLOBAL)) { json }
            }, notificationRepositoriesModule)
        }
        val koin = application.koin
        val repository = koin.get<NotificationRepository>()
        assertEquals(true, (koin.get<NotificationBadgeProvider>().hasUnread() as AppResult.Success).value)
        cache.entry = NotificationCacheEntry("cached", 1L)
        assertIs<AppResult.Success<Unit>>(koin.get<NotificationMutationRepository>().markRead("notification-1"))
        assertEquals(null, cache.entry)

        fakes.failMutations = true
        assertIs<AppResult.Failure>(koin.get<NotificationMutationRepository>().markAllRead())
        assertEquals(repository, koin.get<NotificationRepository>())
        application.close()
    }

    private fun sampleNotification() = NotificationItem(
        id = "notification-1",
        entityType = "FRIEND_REQUEST",
        message = "Hello",
    )

    private class FakeRemote(private val result: AppResult<List<NotificationItem>>) : NotificationRemoteDataSource {
        var calls: Int = 0

        override suspend fun getNoti(): AppResult<List<NotificationItem>> {
            calls += 1
            return result
        }
    }

    private class MemoryCache : NotificationCache {
        var entry: NotificationCacheEntry? = null

        override suspend fun read(): NotificationCacheEntry? = entry
        override suspend fun write(payload: String, updatedAtEpochMillis: Long) {
            entry = NotificationCacheEntry(payload, updatedAtEpochMillis)
        }
        override suspend fun clear() {
            entry = null
        }
    }

    private class NotificationFakes : GetNotificationsApi, PutNotiApi, PutNotiReadAllApi {
        var failMutations = false
        override suspend fun getNotifications(): List<NotificationItem> = listOf(sampleNotificationStatic())
        override suspend fun putNoti(id: String) {
            if (failMutations) error("mutation failed")
        }
        override suspend fun putNotiReadAll() {
            if (failMutations) error("mutation failed")
        }
    }

    private companion object {
        fun sampleNotificationStatic() = NotificationItem(
            id = "notification-1",
            entityType = "FRIEND_REQUEST",
            message = "Hello",
            isRead = false,
        )
    }

    private object TestLogger : com.wealthvault.core.observability.AppLogger {
        override fun debug(message: String) = Unit
        override fun info(message: String) = Unit
        override fun warn(message: String, cause: Throwable?) = Unit
        override fun error(message: String, cause: Throwable?) = Unit
    }
}
