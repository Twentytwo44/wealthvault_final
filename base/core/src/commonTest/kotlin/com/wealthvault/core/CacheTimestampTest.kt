package com.wealthvault.core

import com.wealthvault.core.cache.cacheAgeMillis
import com.wealthvault.core.cache.isCacheFresh
import com.wealthvault.core.cache.sanitizeCacheTimestamp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CacheTimestampTest {
    @Test
    fun farPastTimestampCannotOverflowIntoFreshCache() {
        val now = 10_000L

        assertEquals(Long.MAX_VALUE, cacheAgeMillis(Long.MIN_VALUE, now))
        assertFalse(isCacheFresh(Long.MIN_VALUE, now, ttlMillis = 15 * 60 * 1_000L))
    }

    @Test
    fun futureTimestampIsConservativelyStaleUntilSanitized() {
        val now = 10_000L
        val sanitized = sanitizeCacheTimestamp(now + 1_000L, now, ttlMillis = 500L)

        assertEquals(10_000L - 501L, sanitized)
        assertEquals(501L, cacheAgeMillis(sanitized, now))
        assertTrue(isCacheFresh(sanitized, now, ttlMillis = 500L).not())
        assertFalse(isCacheFresh(now + 1_000L, now, ttlMillis = 500L))
    }

    @Test
    fun normalTimestampUsesNonNegativeAge() {
        assertEquals(250L, cacheAgeMillis(9_750L, 10_000L))
        assertTrue(isCacheFresh(9_750L, 10_000L, ttlMillis = 250L))
        assertFalse(isCacheFresh(9_749L, 10_000L, ttlMillis = 250L))
    }
}
