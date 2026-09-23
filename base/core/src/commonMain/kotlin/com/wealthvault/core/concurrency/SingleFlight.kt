package com.wealthvault.core.concurrency

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Shares one in-flight computation for a key among concurrent callers.
 *
 * The owner completes the deferred for all waiters. Cancellation of a waiter
 * does not cancel the shared operation; cancellation of the owner completes the
 * flight exceptionally and removes it so a later caller can retry.
 */
class SingleFlight<K> {
    private val mutex = Mutex()
    private val flights = mutableMapOf<K, CompletableDeferred<Any?>>()

    @Suppress("UNCHECKED_CAST")
    suspend fun <T> execute(key: K, block: suspend () -> T): T {
        val selection = mutex.withLock {
            flights[key]?.let { FlightSelection(it, owner = false) }
                ?: CompletableDeferred<Any?>().also { flight ->
                    flights[key] = flight
                }.let { flight -> FlightSelection(flight, owner = true) }
        }

        if (!selection.owner) return selection.flight.await() as T

        return try {
            val result = block()
            selection.flight.complete(result)
            result
        } catch (error: CancellationException) {
            selection.flight.completeExceptionally(error)
            throw error
        } catch (error: Throwable) {
            selection.flight.completeExceptionally(error)
            throw error
        } finally {
            mutex.withLock {
                if (flights[key] === selection.flight) flights.remove(key)
            }
        }
    }

    private data class FlightSelection(
        val flight: CompletableDeferred<Any?>,
        val owner: Boolean,
    )
}
