package com.wealthvault.setup_api

import com.wealthvault.domain.auth.SessionTokenStore
import com.wealthvault.domain.auth.SessionTokens
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import io.ktor.http.HttpMethod
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SessionRefreshCoordinatorTest {
    @Test
    fun concurrentUnauthorizedRequestsShareOneRefresh() = runBlocking {
        val store = FakeSessionStore()
        var refreshCalls = 0
        val coordinator = SessionRefreshCoordinator(store) {
            refreshCalls += 1
            delay(10)
            store.saveTokens(SessionTokens("new-access", "new-refresh"))
            "new-access"
        }

        val tokens = (1..20).map {
            async { coordinator.refreshAfterUnauthorized("expired-access") }
        }.awaitAll()

        assertEquals(1, refreshCalls)
        assertEquals(listOf("new-access"), tokens.distinct())
    }

    @Test
    fun concurrentUnauthorizedRequestsShareOneFailedRefresh() = runBlocking {
        val store = FakeSessionStore()
        var refreshCalls = 0
        val coordinator = SessionRefreshCoordinator(store) {
            refreshCalls += 1
            delay(10)
            null
        }

        val tokens = (1..20).map {
            async { coordinator.refreshAfterUnauthorized("expired-access") }
        }.awaitAll()

        assertEquals(1, refreshCalls)
        assertEquals(listOf(null), tokens.distinct())
    }

    @Test
    fun clearedSessionStillSharesOneFailedRefresh() = runBlocking {
        val store = FakeSessionStore()
        var refreshCalls = 0
        val coordinator = SessionRefreshCoordinator(store) {
            refreshCalls += 1
            delay(10)
            store.clearTokens()
            null
        }

        val tokens = (1..20).map {
            async { coordinator.refreshAfterUnauthorized("expired-access") }
        }.awaitAll()

        assertEquals(1, refreshCalls)
        assertEquals(listOf(null), tokens.distinct())
    }

    @Test
    fun aLaterUnauthorizedRequestCanRetryAfterFailedFlight() = runBlocking {
        val store = FakeSessionStore()
        var refreshCalls = 0
        val coordinator = SessionRefreshCoordinator(store) {
            refreshCalls += 1
            if (refreshCalls == 1) null else {
                store.saveTokens(SessionTokens("retried-access", "retried-refresh"))
                "retried-access"
            }
        }

        assertEquals(null, coordinator.refreshAfterUnauthorized("expired-access"))
        assertEquals("retried-access", coordinator.refreshAfterUnauthorized("expired-access"))
        assertEquals(2, refreshCalls)
    }

    @Test
    fun newerSessionTokenSkipsRefreshAfterUnauthorized() = runBlocking {
        val store = FakeSessionStore().apply {
            saveTokens(SessionTokens("newer-access", "newer-refresh"))
        }
        var refreshCalls = 0
        val coordinator = SessionRefreshCoordinator(store) {
            refreshCalls += 1
            "should-not-run"
        }

        assertEquals("newer-access", coordinator.refreshAfterUnauthorized("expired-access"))
        assertEquals(0, refreshCalls)
    }

    @Test
    fun exceptionFromOwnerIsSharedAndNextRequestCanRetry() = runBlocking {
        val store = FakeSessionStore()
        var refreshCalls = 0
        val entered = CompletableDeferred<Unit>()
        val release = CompletableDeferred<Unit>()
        val coordinator = SessionRefreshCoordinator(store) {
            refreshCalls += 1
            if (refreshCalls == 1) {
                entered.complete(Unit)
                release.await()
                error("refresh unavailable")
            }
            store.saveTokens(SessionTokens("recovered-access", "recovered-refresh"))
            "recovered-access"
        }

        val owner = async { runCatching { coordinator.refreshAfterUnauthorized("expired-access") } }
        entered.await()
        val joined = async { runCatching { coordinator.refreshAfterUnauthorized("expired-access") } }
        delay(10)
        release.complete(Unit)
        assertTrue(owner.await().isFailure)
        assertTrue(joined.await().isFailure)
        assertEquals("recovered-access", coordinator.refreshAfterUnauthorized("expired-access"))
        assertEquals(2, refreshCalls)
    }

    @Test
    fun differentExpiredTokensDoNotJoinTheSameFlight() = runBlocking {
        val store = FakeSessionStore(initialAccess = null)
        var refreshCalls = 0
        val firstEntered = CompletableDeferred<Unit>()
        val secondEntered = CompletableDeferred<Unit>()
        val releaseFirst = CompletableDeferred<Unit>()
        val coordinator = SessionRefreshCoordinator(store) {
            refreshCalls += 1
            if (refreshCalls == 1) {
                firstEntered.complete(Unit)
                releaseFirst.await()
            } else {
                secondEntered.complete(Unit)
            }
            null
        }

        val first = async { coordinator.refreshAfterUnauthorized("expired-a") }
        firstEntered.await()
        val second = async { coordinator.refreshAfterUnauthorized("expired-b") }
        secondEntered.await()
        releaseFirst.complete(Unit)
        assertEquals(null, first.await())
        assertEquals(null, second.await())
        assertEquals(2, refreshCalls)
    }

    @Test
    fun missingFailedTokenStillUsesSingleFlightRefresh() = runBlocking {
        val store = FakeSessionStore(initialAccess = null)
        var refreshCalls = 0
        val coordinator = SessionRefreshCoordinator(store) {
            refreshCalls += 1
            null
        }

        assertEquals(null, coordinator.refreshAfterUnauthorized(null))
        assertEquals(1, refreshCalls)
    }

    @Test
    fun blankLatestTokenIsNotAcceptedAsAnAuthenticatedSession() = runBlocking {
        val store = FakeSessionStore(initialAccess = "   ")
        var refreshCalls = 0
        val coordinator = SessionRefreshCoordinator(store) {
            refreshCalls += 1
            null
        }

        assertEquals(null, coordinator.refreshAfterUnauthorized("expired-access"))
        assertEquals(1, refreshCalls)
    }

    @Test
    fun failedOldFlightDoesNotClearANewerFlight() = runBlocking {
        val store = FakeSessionStore(initialAccess = null)
        var refreshCalls = 0
        val firstEntered = CompletableDeferred<Unit>()
        val secondEntered = CompletableDeferred<Unit>()
        val releaseFirst = CompletableDeferred<Unit>()
        val releaseSecond = CompletableDeferred<Unit>()
        val coordinator = SessionRefreshCoordinator(store) {
            refreshCalls += 1
            if (refreshCalls == 1) {
                firstEntered.complete(Unit)
                releaseFirst.await()
                error("old refresh failed")
            }
            secondEntered.complete(Unit)
            releaseSecond.await()
            null
        }

        val first = async { runCatching { coordinator.refreshAfterUnauthorized("expired-a") } }
        firstEntered.await()
        val second = async { coordinator.refreshAfterUnauthorized("expired-b") }
        secondEntered.await()
        releaseFirst.complete(Unit)
        assertTrue(first.await().isFailure)
        releaseSecond.complete(Unit)
        assertEquals(null, second.await())
        assertEquals(2, refreshCalls)
    }

    @Test
    fun unsafeMutationsAreNeverMarkedReplayableAfter401() {
        assertTrue(HttpMethod.Get.isIdempotent())
        assertTrue(HttpMethod.Put.isIdempotent())
        assertFalse(HttpMethod.Post.isIdempotent())
        assertFalse(HttpMethod.Patch.isIdempotent())
        assertFalse(HttpMethod.Delete.isIdempotent())
    }

    private class FakeSessionStore(initialAccess: String? = "expired-access") : SessionTokenStore {
        private val access = MutableStateFlow<String?>(initialAccess)
        private val refresh = MutableStateFlow<String?>("refresh")

        override val accessToken: Flow<String?> = access
        override val refreshToken: Flow<String?> = refresh

        override suspend fun saveTokens(tokens: SessionTokens) {
            access.value = tokens.accessToken
            refresh.value = tokens.refreshToken
        }

        override suspend fun clearTokens() {
            access.value = null
            refresh.value = null
        }
    }
}
