package com.wealthvault.database

import com.wealthvault.core.cache.FeatureCache
import com.wealthvault.core.cache.FeatureCacheEntry
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/** SQLDelight-backed cache for bounded-context application snapshots. */
class FeatureCacheStore(
    private val database: WealthVaultDatabase,
) : FeatureCache {
    private val mutex = Mutex()

    override suspend fun read(namespace: String, key: String): FeatureCacheEntry? = mutex.withLock {
        database.featureSnapshotQueries.selectByKey(namespace, key).executeAsOneOrNull()?.let {
            FeatureCacheEntry(
                payload = it.payload,
                updatedAtEpochMillis = it.updated_at_epoch_millis,
            )
        }
    }

    override suspend fun write(
        namespace: String,
        key: String,
        payload: String,
        updatedAtEpochMillis: Long,
    ) = mutex.withLock {
        database.featureSnapshotQueries.upsert(namespace, key, payload, updatedAtEpochMillis)
        Unit
    }

    override suspend fun clear(namespace: String, key: String) = mutex.withLock {
        database.featureSnapshotQueries.deleteByKey(namespace, key)
        Unit
    }

    override suspend fun clearNamespace(namespace: String) = mutex.withLock {
        database.featureSnapshotQueries.deleteByNamespace(namespace)
        Unit
    }

    override suspend fun clearNamespaces(namespaces: Set<String>) = mutex.withLock {
        database.transaction {
            namespaces.forEach { namespace ->
                database.featureSnapshotQueries.deleteByNamespace(namespace)
            }
        }
        Unit
    }
}
