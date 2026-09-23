package com.wealthvault.data.notification

import com.wealthvault.config.Config
import com.wealthvault.core.KoinConst
import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.architecture.CacheFreshness
import com.wealthvault.core.architecture.CachedValue
import com.wealthvault.core.architecture.toAppError
import com.wealthvault.core.cache.NotificationCache
import com.wealthvault.core.cache.isCacheFresh
import com.wealthvault.core.cache.sanitizeCacheTimestamp
import com.wealthvault.core.concurrency.SingleFlight
import com.wealthvault.core.model.NotificationItem
import com.wealthvault.core.notification.NotificationBadgeProvider
import com.wealthvault.core.observability.AppLogger
import com.wealthvault.core.observability.NoOpAppLogger
import com.wealthvault.core.observability.NoOpPerformanceTracer
import com.wealthvault.core.observability.PerformanceTracer
import com.wealthvault.domain.notification.NotificationMutationRepository
import com.wealthvault.domain.notification.NotificationRepository
import com.wealthvault.domain.notification.NotificationSnapshot
import com.wealthvault.domain.social.AcceptFriendRequest
import com.wealthvault.domain.social.AcceptFriendResult
import com.wealthvault.data.notification.transport.notification.GetNotificationsApi
import com.wealthvault.data.notification.transport.read.PutNotiApi
import com.wealthvault.data.notification.transport.readall.PutNotiReadAllApi
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal interface NotificationRemoteDataSource {
    suspend fun getNoti(): AppResult<List<NotificationItem>>
}

private class NotificationDataSource(
    private val notificationApi: GetNotificationsApi,
    private val logger: AppLogger = NoOpAppLogger,
) : NotificationRemoteDataSource {
    override suspend fun getNoti(): AppResult<List<NotificationItem>> = try {
        val items = notificationApi.getNotifications()
        logger.debug("Notification list received")
        AppResult.Success(items)
    } catch (error: CancellationException) {
        throw error
    } catch (error: Throwable) {
        AppResult.Failure(error.toAppError())
    }
}

internal class NotificationRepositoryImpl(
    private val networkDataSource: NotificationRemoteDataSource,
    private val cache: NotificationCache,
    private val json: Json,
    private val logger: AppLogger = NoOpAppLogger,
    private val tracer: PerformanceTracer = NoOpPerformanceTracer,
) : NotificationRepository {
    private val observed = MutableStateFlow<CachedValue<List<NotificationItem>>?>(null)
    private val refreshes = SingleFlight<String>()

    override fun observe(): Flow<CachedValue<List<NotificationItem>>> = observed
        .filterNotNull()
        .distinctUntilChanged()

    override suspend fun refresh(force: Boolean): AppResult<Unit> = when (val result = getNoti(force)) {
        is AppResult.Success -> AppResult.Success(Unit)
        is AppResult.Failure -> AppResult.Failure(result.error)
    }

    override suspend fun invalidate() {
        try {
            cache.clear()
        } catch (error: Throwable) {
            if (error is CancellationException) throw error
            logger.warn("Notification cache clear failed", error)
        } finally {
            observed.value = null
        }
    }

    override suspend fun getNoti(forceRefresh: Boolean): AppResult<NotificationSnapshot> =
        refreshes.execute(NOTIFICATION_KEY) { getNotiInternal(forceRefresh) }

    private suspend fun getNotiInternal(forceRefresh: Boolean): AppResult<NotificationSnapshot> {
        val now = kotlin.time.Clock.System.now().toEpochMilliseconds()
        val cached = try {
            cache.read()?.let { entry ->
                runCatching { json.decodeFromString<List<NotificationItem>>(entry.payload) }
                    .onFailure { error ->
                        if (error is CancellationException) throw error
                        logger.warn("Notification cache decode failed", error)
                    }
                    .getOrNull()
                    ?.let { value ->
                        val updatedAt = sanitizeCacheTimestamp(
                            entry.updatedAtEpochMillis,
                            now,
                            NOTIFICATION_TTL_MILLIS,
                        )
                        CachedNotifications(value, updatedAt)
                    }
            }
        } catch (error: Throwable) {
            if (error is CancellationException) throw error
            logger.warn("Notification cache read failed", error)
            null
        }

        if (!forceRefresh && cached != null && isCacheFresh(cached.updatedAtEpochMillis, now, NOTIFICATION_TTL_MILLIS)) {
            observed.value = CachedValue(cached.value, CacheFreshness.Fresh, cached.updatedAtEpochMillis)
            return AppResult.Success(NotificationSnapshot(cached.value, CacheFreshness.Fresh))
        }

        cached?.let {
            observed.value = CachedValue(
                value = it.value,
                freshness = CacheFreshness.Stale,
                updatedAtEpochMillis = it.updatedAtEpochMillis,
            )
        }

        val networkResult = tracer.trace("notification.network.refresh") {
            networkDataSource.getNoti()
        }
        return when (networkResult) {
            is AppResult.Success -> {
                runCatching { cache.write(json.encodeToString(networkResult.value), now) }
                    .onFailure { error ->
                        if (error is CancellationException) throw error
                        logger.warn("Notification cache write failed", error)
                    }
                observed.value = CachedValue(networkResult.value, CacheFreshness.Fresh, now)
                AppResult.Success(NotificationSnapshot(networkResult.value, CacheFreshness.Fresh))
            }
            is AppResult.Failure -> {
                cached?.let {
                    observed.value = CachedValue(it.value, CacheFreshness.Offline, it.updatedAtEpochMillis)
                    AppResult.Success(NotificationSnapshot(it.value, CacheFreshness.Offline))
                } ?: AppResult.Failure(networkResult.error)
            }
        }
    }

    private data class CachedNotifications(
        val value: List<NotificationItem>,
        val updatedAtEpochMillis: Long,
    )

    private companion object {
        const val NOTIFICATION_KEY = "notifications"
        const val NOTIFICATION_TTL_MILLIS = 30 * 1_000L
    }
}

private class PutNotificationDataSource(
    private val putNotificationApi: PutNotiApi,
    private val putNotiReadAllApi: PutNotiReadAllApi,
    private val logger: AppLogger = NoOpAppLogger,
) {
    suspend fun putNoti(id: String): AppResult<Unit> = try {
        putNotificationApi.putNoti(id)
        logger.debug("Notification marked as read on server")
        AppResult.Success(Unit)
    } catch (error: CancellationException) {
        throw error
    } catch (error: Throwable) {
        AppResult.Failure(error.toAppError())
    }

    suspend fun putReadAllNoti(): AppResult<Unit> = try {
        putNotiReadAllApi.putNotiReadAll()
        logger.debug("All notifications marked as read on server")
        AppResult.Success(Unit)
    } catch (error: CancellationException) {
        throw error
    } catch (error: Throwable) {
        AppResult.Failure(error.toAppError())
    }
}

private class PutNotificationRepositoryImpl(
    private val networkDataSource: PutNotificationDataSource,
    private val cache: NotificationCache,
    private val logger: AppLogger = NoOpAppLogger,
) : NotificationMutationRepository {
    override suspend fun markRead(id: String): AppResult<Unit> = when (val result = networkDataSource.putNoti(id)) {
        is AppResult.Success -> {
            clearCacheBestEffort()
            result
        }
        is AppResult.Failure -> result
    }

    override suspend fun markAllRead(): AppResult<Unit> =
        when (val result = networkDataSource.putReadAllNoti()) {
            is AppResult.Success -> {
                clearCacheBestEffort()
                result
            }
            is AppResult.Failure -> result
        }

    private suspend fun clearCacheBestEffort() {
        try {
            cache.clear()
        } catch (error: Throwable) {
            if (error is CancellationException) throw error
            logger.warn("Notification cache clear after mutation failed", error)
        }
    }
}

private class NotificationBadgeProviderImpl(
    private val notificationRepository: NotificationRepository,
) : NotificationBadgeProvider {
    override suspend fun hasUnread(): AppResult<Boolean> = try {
        when (val result = notificationRepository.getNoti()) {
            is AppResult.Success -> AppResult.Success(
                result.value.value.any { notification -> notification.isRead != true },
            )
            is AppResult.Failure -> result
        }
    } catch (error: CancellationException) {
        throw error
    } catch (error: Throwable) {
        AppResult.Failure(error.toAppError())
    }
}

internal interface AcceptFriendTransport {
    suspend fun acceptFriend(requesterId: String, action: String): String?
}

private class KtorAcceptFriendTransport(
    private val client: HttpClient,
) : AcceptFriendTransport {
    override suspend fun acceptFriend(requesterId: String, action: String): String? {
        val response = client.post("${Config.localhost_android}friend/accept") {
            setBody(AcceptFriendWireRequest(requesterId = requesterId, action = action))
        }.body<AcceptFriendWireResponse>()
        response.error?.let { throw IllegalStateException(it) }
        return response.data?.success
    }
}

@Serializable
private data class AcceptFriendWireRequest(
    @SerialName("requester_id") val requesterId: String,
    @SerialName("action") val action: String,
)

@Serializable
private data class AcceptFriendWireResponse(
    @SerialName("status") val status: String? = null,
    @SerialName("data") val data: AcceptFriendWireData? = null,
    @SerialName("error") val error: String? = null,
)

@Serializable
private data class AcceptFriendWireData(
    @SerialName("success") val success: String? = null,
)

internal class AcceptFriendDataSource(
    private val transport: AcceptFriendTransport,
    private val logger: AppLogger = NoOpAppLogger,
) {
    suspend fun acceptFriend(request: AcceptFriendRequest): AppResult<AcceptFriendResult> = try {
        val success = transport.acceptFriend(request.requesterId, request.action)
        logger.debug("Friend request accepted")
        AppResult.Success(AcceptFriendResult(success = success))
    } catch (error: CancellationException) {
        throw error
    } catch (error: Throwable) {
        AppResult.Failure(error.toAppError())
    }
}

private class AcceptFriendRepositoryImpl(
    private val networkDataSource: AcceptFriendDataSource,
) {
    suspend fun acceptFriend(request: AcceptFriendRequest): AppResult<AcceptFriendResult> =
        networkDataSource.acceptFriend(request)
}

/** Data-layer registrations shared by notification and dashboard features. */
internal val notificationRepositoriesModule = module {
    factory { NotificationDataSource(get(), get()) }
    factory<NotificationRemoteDataSource> { get<NotificationDataSource>() }
    single<NotificationRepositoryImpl> {
        NotificationRepositoryImpl(
            networkDataSource = get(),
            cache = get(),
            json = get(named(KoinConst.KotlinSerialization.GLOBAL)),
            logger = get(),
            tracer = get(),
        )
    }
    single<NotificationRepository> { get<NotificationRepositoryImpl>() }

    factory { PutNotificationDataSource(get(), get(), get()) }
    single<NotificationMutationRepository> { PutNotificationRepositoryImpl(get(), get()) }
    single<NotificationBadgeProvider> { NotificationBadgeProviderImpl(get()) }

    factory<AcceptFriendTransport> {
        KtorAcceptFriendTransport(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL)))
    }
    factory { AcceptFriendDataSource(get(), get()) }
    single<AcceptFriendRepositoryImpl> { AcceptFriendRepositoryImpl(get()) }
}
