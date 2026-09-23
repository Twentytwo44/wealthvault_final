package com.wealthvault.setup_api

import com.wealthvault.domain.auth.SessionTokenStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Serializes refreshes after an authentication failure.
 *
 * Requests that observe the same expired access token share one refresh call;
 * requests that arrive after a successful refresh reuse the newer token. The
 * coordinator is deliberately independent from Ktor so it can be tested with
 * a fake session store and cannot accidentally recurse through an authenticated
 * HTTP client.
 */
class SessionRefreshCoordinator(
    private val sessionStore: SessionTokenStore,
    private val refresh: suspend () -> String?,
) {
    private val mutex = Mutex()
    private var activeFlight: RefreshFlight? = null

    suspend fun refreshAfterUnauthorized(failedAccessToken: String?): String? {
        val latestToken = sessionStore.accessToken.first()
        if (!latestToken.isNullOrBlank() && latestToken != failedAccessToken) {
            return latestToken
        }

        val selection = mutex.withLock {
            activeFlight
                ?.takeIf { it.failedAccessToken == failedAccessToken }
                ?.let { FlightSelection(it, owner = false) }
                ?: RefreshFlight(failedAccessToken).also { flight ->
                    activeFlight = flight
                }.let { FlightSelection(it, owner = true) }
        }

        if (!selection.owner) return selection.flight.result.await()

        return try {
            val result = refresh()
            mutex.withLock {
                // Complete and clear while holding the same lock used for
                // selection. A later request can retry after a failed flight,
                // while callers that already joined still receive its result.
                selection.flight.result.complete(result)
                if (activeFlight === selection.flight) activeFlight = null
            }
            result
        } catch (error: Throwable) {
            mutex.withLock {
                selection.flight.result.completeExceptionally(error)
                if (activeFlight === selection.flight) activeFlight = null
            }
            throw error
        }
    }

    private data class FlightSelection(
        val flight: RefreshFlight,
        val owner: Boolean,
    )

    private data class RefreshFlight(
        val failedAccessToken: String?,
        val result: CompletableDeferred<String?> = CompletableDeferred(),
    )
}
