package com.wealthvault.database

import com.wealthvault.core.cache.NotificationCache
import com.wealthvault.core.cache.NotificationCacheEntry
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class NotificationCacheStore(
    private val database: WealthVaultDatabase,
) : NotificationCache {
    private val mutex = Mutex()

    override suspend fun read(): NotificationCacheEntry? = mutex.withLock {
        database.notificationSnapshotQueries.selectLatest().executeAsOneOrNull()?.let {
            NotificationCacheEntry(
                payload = it.payload,
                updatedAtEpochMillis = it.updated_at_epoch_millis,
            )
        }
    }

    override suspend fun write(payload: String, updatedAtEpochMillis: Long) {
        mutex.withLock {
            database.notificationSnapshotQueries.upsert(payload, updatedAtEpochMillis)
        }
    }

    override suspend fun clear() {
        mutex.withLock {
            database.notificationSnapshotQueries.clear()
        }
    }
}
