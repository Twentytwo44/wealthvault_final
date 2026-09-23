package com.wealthvault.core.cache

/**
 * Small key/value cache boundary shared by application-data repositories.
 * Implementations own persistence details; feature code only stores encoded
 * snapshots and timestamps.
 */
interface FeatureCache {
    suspend fun read(namespace: String, key: String): FeatureCacheEntry?

    suspend fun write(namespace: String, key: String, payload: String, updatedAtEpochMillis: Long)

    suspend fun clear(namespace: String, key: String)

    suspend fun clearNamespace(namespace: String)

    /**
     * Clears several namespaces as one logical invalidation.
     *
     * Lightweight test stores keep the compatibility default; persistent
     * implementations should override this method and perform the deletes in
     * one database transaction.
     */
    suspend fun clearNamespaces(namespaces: Set<String>) {
        namespaces.forEach { clearNamespace(it) }
    }
}

data class FeatureCacheEntry(
    val payload: String,
    val updatedAtEpochMillis: Long,
)
