package com.wealthvault.database

import com.wealthvault.core.cache.DashboardCache
import com.wealthvault.core.cache.DashboardCacheEntry
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class DashboardCacheStore(
    private val database: WealthVaultDatabase,
) : DashboardCache {
    private val mutex = Mutex()

    override suspend fun read(): DashboardCacheEntry? = mutex.withLock {
        database.dashboardSnapshotQueries.selectLatest().executeAsOneOrNull()?.let {
            DashboardCacheEntry(
                payload = it.payload,
                updatedAtEpochMillis = it.updated_at_epoch_millis,
            )
        }
    }

    override suspend fun write(payload: String, updatedAtEpochMillis: Long) {
        mutex.withLock {
            database.dashboardSnapshotQueries.upsert(payload, updatedAtEpochMillis)
        }
    }

    override suspend fun clear() {
        mutex.withLock {
            database.dashboardSnapshotQueries.clear()
        }
    }
}
