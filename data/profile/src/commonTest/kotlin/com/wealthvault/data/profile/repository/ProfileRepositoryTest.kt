package com.wealthvault.data.profile.repository

import com.wealthvault.core.architecture.AppError
import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.architecture.CacheFreshness
import com.wealthvault.core.cache.FeatureCache
import com.wealthvault.core.cache.FeatureCacheEntry
import com.wealthvault.core.observability.AppLogger
import com.wealthvault.domain.profile.CloseFriendData
import com.wealthvault.domain.profile.FriendData
import com.wealthvault.domain.profile.UpdateUserData
import com.wealthvault.domain.profile.UpdateUserDataRequest
import com.wealthvault.domain.profile.UserData
import com.wealthvault.data.profile.repository.ProfileRemoteDataSource
import com.wealthvault.data.profile.repository.ProfileRepositoryImpl
import com.wealthvault.data.profile.repository.toCacheRecord
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.time.Clock

class ProfileRepositoryTest {
    private val json = Json

    @Test
    fun freshCacheAvoidsDuplicateUserRequest() = runTest {
        val remote = FakeRemote(AppResult.Success(sampleUser()))
        val cache = MemoryCache()
        val repository = ProfileRepositoryImpl(remote, TestLogger, cache, json)

        assertIs<AppResult.Success<*>>(repository.getUser(force = true))
        assertIs<AppResult.Success<*>>(repository.getUser())

        assertEquals(1, remote.userCalls)
    }

    @Test
    fun concurrentForcedRefreshesShareOneUserRequest() = runTest {
        val remote = FakeRemote(AppResult.Success(sampleUser()), delayMillis = 10)
        val repository = ProfileRepositoryImpl(remote, TestLogger, MemoryCache(), json)

        (1..20).map {
            async { repository.getUser(force = true) }
        }.awaitAll()

        assertEquals(1, remote.userCalls)
    }

    @Test
    fun staleCacheIsReturnedWhenProfileRequestFails() = runTest {
        val cached = sampleUser()
        val cache = MemoryCache().apply {
            values["user"] = FeatureCacheEntry(
                payload = json.encodeToString(cached.toCacheRecord()),
                updatedAtEpochMillis = Clock.System.now().toEpochMilliseconds() - (5 * 60 * 1_000L + 1),
            )
        }
        val repository = ProfileRepositoryImpl(
            FakeRemote(AppResult.Failure(AppError.Network(IllegalStateException("offline")))),
            TestLogger,
            cache,
            json,
        )

        val result = assertIs<AppResult.Success<*>>(repository.getUser())
        assertEquals(cached, result.value)
        assertEquals(CacheFreshness.Offline, repository.observeUser().first().freshness)
    }

    @Test
    fun delegatesAllProfileReadsAndMutationsAndInvalidatesSnapshots() = runTest {
        val remote = FullRemote()
        val cache = MemoryCache()
        val repository = ProfileRepositoryImpl(remote, TestLogger, cache, json)

        assertEquals(FULL_CLOSE_FRIENDS, repository.getCloseFriends(force = true).getOrThrow())
        assertEquals(FULL_FRIENDS, repository.getAllFriends(force = true).getOrThrow())
        assertEquals(FULL_USER, repository.getUser(force = true).getOrThrow())
        assertEquals(FULL_UPDATE, repository.updateUserData(UpdateUserDataRequest("u", "f", "l", "b", "p")).getOrThrow())
        assertEquals(true, repository.setCloseFriend("friend-1", true).getOrThrow())
        assertEquals(Unit, repository.refresh(force = true).getOrThrow())
        assertEquals(1, remote.updateUserCalls)
        assertEquals(1, remote.closeFriendCalls)
    }

    private fun sampleUser() = UserData(id = "user-1", username = "alice", email = "alice@example.com")

    private class FakeRemote(
        private val userResult: AppResult<UserData>,
        private val delayMillis: Long = 0,
    ) : ProfileRemoteDataSource {
        var userCalls = 0

        override suspend fun getUser(): AppResult<UserData> {
            userCalls += 1
            if (delayMillis > 0) delay(delayMillis)
            return userResult
        }

        override suspend fun getCloseFriends(): AppResult<List<CloseFriendData>> = AppResult.Success(emptyList())

        override suspend fun updateUserData(request: UpdateUserDataRequest): AppResult<UpdateUserData> =
            AppResult.Failure(AppError.Unknown(UnsupportedOperationException()))

        override suspend fun updateCloseFriendStatus(friendId: String, isClose: Boolean): AppResult<Boolean> =
            AppResult.Failure(AppError.Unknown(UnsupportedOperationException()))

        override suspend fun getAllFriends(): AppResult<List<FriendData>> = AppResult.Success(emptyList())
    }

    private class FullRemote : ProfileRemoteDataSource {
        var updateUserCalls = 0
        var closeFriendCalls = 0

        override suspend fun getUser() = AppResult.Success(FULL_USER)
        override suspend fun getCloseFriends() = AppResult.Success(FULL_CLOSE_FRIENDS)
        override suspend fun updateUserData(request: UpdateUserDataRequest): AppResult<UpdateUserData> {
            updateUserCalls += 1
            return AppResult.Success(FULL_UPDATE)
        }

        override suspend fun updateCloseFriendStatus(friendId: String, isClose: Boolean): AppResult<Boolean> {
            closeFriendCalls += 1
            return AppResult.Success(true)
        }

        override suspend fun getAllFriends() = AppResult.Success(FULL_FRIENDS)
    }

    private class MemoryCache : FeatureCache {
        val values = mutableMapOf<String, FeatureCacheEntry>()

        override suspend fun read(namespace: String, key: String): FeatureCacheEntry? = values[key]

        override suspend fun write(namespace: String, key: String, payload: String, updatedAtEpochMillis: Long) {
            values[key] = FeatureCacheEntry(payload, updatedAtEpochMillis)
        }

        override suspend fun clear(namespace: String, key: String) {
            values.remove(key)
        }

        override suspend fun clearNamespace(namespace: String) {
            values.clear()
        }
    }

    private object TestLogger : AppLogger {
        override fun debug(message: String) = Unit
        override fun info(message: String) = Unit
        override fun warn(message: String, cause: Throwable?) = Unit
        override fun error(message: String, cause: Throwable?) = Unit
    }

    private companion object {
        val FULL_USER = UserData(id = "user-full", username = "full", email = "full@example.com")
        val FULL_CLOSE_FRIENDS = listOf(
            CloseFriendData("friend-1", "friend", "friend@example.com", "First", "Last", "123", "profile", "2000", 25, true, "created", "updated", true),
        )
        val FULL_FRIENDS = listOf(FriendData(id = "friend-2", username = "other", isFriend = true))
        val FULL_UPDATE = UpdateUserData(id = "user-full", username = "full-updated")
    }
}
