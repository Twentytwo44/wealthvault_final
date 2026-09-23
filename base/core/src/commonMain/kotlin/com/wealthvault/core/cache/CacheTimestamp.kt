package com.wealthvault.core.cache

/**
 * Normalizes timestamps read from persistent storage before TTL arithmetic.
 *
 * A clock change, a corrupt migration, or a maliciously edited snapshot can
 * otherwise put the timestamp in the future and make stale content look fresh
 * for an entire TTL window. Treating that value as older than the TTL forces a
 * refresh while still allowing the snapshot to be used as an offline fallback.
 */
fun sanitizeCacheTimestamp(
    storedAtEpochMillis: Long,
    nowEpochMillis: Long,
    ttlMillis: Long,
): Long = if (storedAtEpochMillis > nowEpochMillis) {
    nowEpochMillis - ttlMillis - 1L
} else {
    storedAtEpochMillis
}

/**
 * Returns a non-negative cache age without allowing epoch subtraction to
 * overflow. A timestamp far in the past is corrupt/stale, not a fresh value
 * with a negative age.
 */
fun cacheAgeMillis(
    storedAtEpochMillis: Long,
    nowEpochMillis: Long,
): Long = when {
    storedAtEpochMillis > nowEpochMillis -> Long.MAX_VALUE
    storedAtEpochMillis == Long.MIN_VALUE -> Long.MAX_VALUE
    else -> (nowEpochMillis - storedAtEpochMillis).takeIf { it >= 0L } ?: Long.MAX_VALUE
}

/**
 * Shared freshness predicate for repositories. Callers should sanitize a
 * future timestamp before publishing it, but this helper remains conservative
 * if a raw storage value is passed by mistake.
 */
fun isCacheFresh(
    storedAtEpochMillis: Long,
    nowEpochMillis: Long,
    ttlMillis: Long,
): Boolean = cacheAgeMillis(storedAtEpochMillis, nowEpochMillis) <= ttlMillis
