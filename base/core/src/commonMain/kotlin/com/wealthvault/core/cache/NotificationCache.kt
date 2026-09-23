package com.wealthvault.core.cache

/** Serialized notification cache contract; storage details stay in the database module. */
interface NotificationCache {
    suspend fun read(): NotificationCacheEntry?

    suspend fun write(payload: String, updatedAtEpochMillis: Long)

    suspend fun clear()
}

data class NotificationCacheEntry(
    val payload: String,
    val updatedAtEpochMillis: Long,
)
