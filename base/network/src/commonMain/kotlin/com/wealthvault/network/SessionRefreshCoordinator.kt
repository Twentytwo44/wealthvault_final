package com.wealthvault.network

import com.wealthvault.domain.auth.SessionTokenStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Serializes refreshes after an authentication failure.
 *
 * All requests for one session share one refresh call while it is in flight;
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
                ?.let { FlightSelection(it, owner = false) }
                ?: RefreshFlight().also { flight ->
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
        val result: CompletableDeferred<String?> = CompletableDeferred(),
    )
}
