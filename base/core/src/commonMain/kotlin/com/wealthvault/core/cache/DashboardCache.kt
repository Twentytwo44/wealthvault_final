package com.wealthvault.core.cache

/**
 * Storage contract used by the dashboard repository.
 *
 * The feature only knows about this contract; SQLDelight and its generated
 * queries stay inside the database module.
 */
interface DashboardCache {
    suspend fun read(): DashboardCacheEntry?

    suspend fun write(payload: String, updatedAtEpochMillis: Long)

    suspend fun clear()
}

data class DashboardCacheEntry(
    val payload: String,
    val updatedAtEpochMillis: Long,
)
