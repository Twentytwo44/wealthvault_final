package com.wealthvault.core.cache

import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.concurrency.SingleFlight
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlin.time.Clock

/**
 * Persistent stale-while-revalidate cache for low-churn reference lists.
 *
 * The cache stores transport values only inside the data layer. Callers map
 * the returned list to domain values before it crosses the repository
 * boundary. A failed refresh returns the last snapshot so forms can remain
 * usable offline.
 */
class ReferenceListCache(
    private val cache: FeatureCache?,
    private val json: Json?,
    private val namespace: String,
    private val key: String,
    private val ttlMillis: Long = DEFAULT_TTL_MILLIS,
) {
    private val singleFlight = SingleFlight<String>()

    suspend fun <T> get(
        serializer: KSerializer<T>,
        force: Boolean = false,
        load: suspend () -> AppResult<List<T>>,
    ): AppResult<List<T>> {
        val now = Clock.System.now().toEpochMilliseconds()
        val cached = read(serializer, now)
        if (!force && cached != null && isCacheFresh(cached.updatedAtEpochMillis, now, ttlMillis)) {
            return AppResult.Success(cached.value)
        }

        return when (val result = singleFlight.execute(key) { load() }) {
            is AppResult.Success -> {
                write(serializer, result.value, now)
                result
            }

            is AppResult.Failure -> cached?.let { AppResult.Success(it.value) } ?: result
        }
    }

    private suspend fun <T> read(serializer: KSerializer<T>, now: Long): CachedReference<T>? = try {
        val entry = cache?.read(namespace, key) ?: return null
        val decoder = json ?: return null
        val decoded = decoder.decodeFromString(ListSerializer(serializer), entry.payload)
        CachedReference(
            decoded,
            sanitizeCacheTimestamp(entry.updatedAtEpochMillis, now, ttlMillis),
        )
    } catch (error: Throwable) {
        if (error is CancellationException) throw error
        null
    }

    private suspend fun <T> write(serializer: KSerializer<T>, value: List<T>, now: Long) {
        val encoder = json ?: return
        val payload = runCatching { encoder.encodeToString(ListSerializer(serializer), value) }.getOrNull() ?: return
        try {
            cache?.write(namespace, key, payload, now)
        } catch (error: Throwable) {
            if (error is CancellationException) throw error
            // Cache persistence is best effort; a successful network read
            // must never be reported as a failure because storage is down.
        }
    }

    private data class CachedReference<T>(
        val value: List<T>,
        val updatedAtEpochMillis: Long,
    )

    private companion object {
        const val DEFAULT_TTL_MILLIS = 15 * 60 * 1_000L
    }
}
