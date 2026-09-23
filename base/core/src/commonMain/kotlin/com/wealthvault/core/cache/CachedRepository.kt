package com.wealthvault.core.cache

import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.architecture.CachedValue
import kotlinx.coroutines.flow.Flow

/** Shared stale-while-revalidate contract for repositories backed by a local cache. */
interface CachedRepository<out T> {
    fun observe(): Flow<CachedValue<T>>

    suspend fun refresh(force: Boolean = false): AppResult<Unit>
}
