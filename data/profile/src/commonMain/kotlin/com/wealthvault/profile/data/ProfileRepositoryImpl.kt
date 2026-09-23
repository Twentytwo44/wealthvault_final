package com.wealthvault.profile.data

import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.architecture.CacheFreshness
import com.wealthvault.core.architecture.CachedValue
import com.wealthvault.core.architecture.toThrowable
import com.wealthvault.core.cache.FeatureCache
import com.wealthvault.core.cache.isCacheFresh
import com.wealthvault.core.cache.sanitizeCacheTimestamp
import com.wealthvault.core.concurrency.SingleFlight
import com.wealthvault.core.observability.AppLogger
import com.wealthvault.core.observability.platformLogger
import com.wealthvault.domain.profile.CloseFriendData
import com.wealthvault.domain.profile.FriendData
import com.wealthvault.domain.profile.ProfileRepository
import com.wealthvault.domain.profile.UpdateUserData
import com.wealthvault.domain.profile.UpdateUserDataRequest
import com.wealthvault.domain.profile.UserData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.time.Clock

internal class ProfileRepositoryImpl(
    private val networkDataSource: ProfileRemoteDataSource,
    private val logger: AppLogger = platformLogger(),
    private val cache: FeatureCache? = null,
    private val json: Json? = null,
) : ProfileRepository {
    private val observedUser = MutableStateFlow<CachedValue<UserData>?>(null)
    private val observedCloseFriends = MutableStateFlow<CachedValue<List<CloseFriendData>>?>(null)
    private val observedAllFriends = MutableStateFlow<CachedValue<List<FriendData>>?>(null)
    private val refreshes = SingleFlight<String>()

    override fun observeUser(): Flow<CachedValue<UserData>> = observedUser.filterNotNull().distinctUntilChanged()

    override fun observeCloseFriends(): Flow<CachedValue<List<CloseFriendData>>> =
        observedCloseFriends.filterNotNull().distinctUntilChanged()

    override fun observeAllFriends(): Flow<CachedValue<List<FriendData>>> =
        observedAllFriends.filterNotNull().distinctUntilChanged()

    override suspend fun getUser(force: Boolean): AppResult<UserData> = cached(
        key = USER_KEY,
        force = force,
        decode = { json?.decodeFromString<UserCacheRecord>(it)?.toDomain() },
        encode = { json?.encodeToString(it.toCacheRecord()) },
        load = { networkDataSource.getUser() },
        publish = { value, freshness, updatedAt -> observedUser.value = CachedValue(value, freshness, updatedAt) },
        successMessage = "Fetched user profile",
        failureMessage = "Get user profile failed",
    )

    override suspend fun getCloseFriends(force: Boolean): AppResult<List<CloseFriendData>> = cached(
        key = CLOSE_FRIENDS_KEY,
        force = force,
        decode = { json?.decodeFromString<List<CloseFriendCacheRecord>>(it)?.map(CloseFriendCacheRecord::toDomain) },
        encode = { json?.encodeToString(it.map(CloseFriendData::toCacheRecord)) },
        load = { networkDataSource.getCloseFriends() },
        publish = { value, freshness, updatedAt -> observedCloseFriends.value = CachedValue(value, freshness, updatedAt) },
        successMessage = "Fetched close friends: {size}",
        failureMessage = "Get close friends failed",
    )

    override suspend fun updateUserData(request: UpdateUserDataRequest): AppResult<UpdateUserData> =
        networkDataSource.updateUserData(request).also { result ->
            when (result) {
                is AppResult.Success -> {
                    clearCached(USER_KEY)
                    observedUser.value = null
                    logger.info("Update user succeeded")
                }
                is AppResult.Failure -> logger.warn("Update user failed", result.error.toThrowable())
            }
        }

    override suspend fun setCloseFriend(friendId: String, isClose: Boolean): AppResult<Boolean> =
        networkDataSource.updateCloseFriendStatus(friendId, isClose).also { result ->
            when (result) {
                is AppResult.Success -> {
                    clearCached(CLOSE_FRIENDS_KEY)
                    clearCached(ALL_FRIENDS_KEY)
                    observedCloseFriends.value = null
                    observedAllFriends.value = null
                    logger.info("Update close friend status succeeded")
                }
                is AppResult.Failure -> logger.warn("Update close friend status failed", result.error.toThrowable())
            }
        }

    override suspend fun getAllFriends(force: Boolean): AppResult<List<FriendData>> = cached(
        key = ALL_FRIENDS_KEY,
        force = force,
        decode = { json?.decodeFromString<List<FriendCacheRecord>>(it)?.map(FriendCacheRecord::toDomain) },
        encode = { json?.encodeToString(it.map(FriendData::toCacheRecord)) },
        load = { networkDataSource.getAllFriends() },
        publish = { value, freshness, updatedAt -> observedAllFriends.value = CachedValue(value, freshness, updatedAt) },
        successMessage = "Fetched all friends: {size}",
        failureMessage = "Get all friends failed",
    )

    override suspend fun refresh(force: Boolean): AppResult<Unit> {
        val results = listOf(
            getUser(force),
            getCloseFriends(force),
            getAllFriends(force),
        )
        return results.firstOrNull { it is AppResult.Failure }?.let { failure ->
            AppResult.Failure((failure as AppResult.Failure).error)
        } ?: AppResult.Success(Unit)
    }

    private suspend inline fun <reified T> cached(
        key: String,
        force: Boolean,
        crossinline decode: (String) -> T?,
        crossinline encode: (T) -> String?,
        crossinline load: suspend () -> AppResult<T>,
        crossinline publish: (T, CacheFreshness, Long) -> Unit,
        successMessage: String,
        failureMessage: String,
    ): AppResult<T> {
        val now = Clock.System.now().toEpochMilliseconds()
        val cached = try {
            cache?.read(NAMESPACE, key)?.let { entry ->
                runCatching { decode(entry.payload) }
                    .onFailure { logger.warn("Profile cache decode failed", it) }
                    .getOrNull()
                    ?.let { value ->
                        CachedSnapshot(
                            value,
                            sanitizeCacheTimestamp(entry.updatedAtEpochMillis, now, PROFILE_TTL_MILLIS),
                        )
                    }
            }
        } catch (error: Throwable) {
            if (error is kotlinx.coroutines.CancellationException) throw error
            logger.warn("Profile cache read failed", error)
            null
        }

        if (!force && cached != null && isCacheFresh(cached.updatedAtEpochMillis, now, PROFILE_TTL_MILLIS)) {
            publish(cached.value, CacheFreshness.Fresh, cached.updatedAtEpochMillis)
            logger.debug(successMessage.replace("{size}", (cached.value as? List<*>)?.size?.toString() ?: ""))
            return AppResult.Success(cached.value)
        }

        cached?.let { publish(it.value, CacheFreshness.Stale, it.updatedAtEpochMillis) }
        // Multiple screens can request the same stale snapshot at once (for
        // example after login and after returning from a mutation). Share the
        // in-flight request so a refresh never produces duplicate GETs.
        return when (val network = refreshes.execute(key) { load() }) {
            is AppResult.Success -> {
                val payload = runCatching { encode(network.value) }
                    .onFailure { logger.warn("Profile cache encode failed", it) }
                    .getOrNull()
                if (payload != null) {
                    try {
                        cache?.write(NAMESPACE, key, payload, now)
                    } catch (error: Throwable) {
                        if (error is kotlinx.coroutines.CancellationException) throw error
                        logger.warn("Profile cache write failed", error)
                    }
                }
                publish(network.value, CacheFreshness.Fresh, now)
                logger.debug(successMessage.replace("{size}", (network.value as? List<*>)?.size?.toString() ?: ""))
                network
            }

            is AppResult.Failure -> {
                logger.warn(failureMessage, network.error.toThrowable())
                cached?.let {
                    publish(it.value, CacheFreshness.Offline, it.updatedAtEpochMillis)
                    AppResult.Success(it.value)
                } ?: network
            }
        }
    }

    private data class CachedSnapshot<T>(val value: T, val updatedAtEpochMillis: Long)

    private suspend fun clearCached(key: String) {
        try {
            cache?.clear(NAMESPACE, key)
        } catch (error: Throwable) {
            if (error is kotlinx.coroutines.CancellationException) throw error
            logger.warn("Profile cache clear failed", error)
        }
    }

    private companion object {
        const val NAMESPACE = "profile"
        const val USER_KEY = "user"
        const val CLOSE_FRIENDS_KEY = "close-friends"
        const val ALL_FRIENDS_KEY = "all-friends"
        const val PROFILE_TTL_MILLIS = 5 * 60 * 1_000L
    }
}
