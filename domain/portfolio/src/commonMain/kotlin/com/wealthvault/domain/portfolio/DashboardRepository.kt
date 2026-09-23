package com.wealthvault.domain.portfolio

import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.architecture.CacheFreshness
import com.wealthvault.core.cache.CachedRepository
import com.wealthvault.core.model.DashboardData

/** Dashboard contract consumed by presentation; transport/cache details stay in data. */
interface DashboardRepository : CachedRepository<DashboardData> {
    suspend fun getDashboardData(forceRefresh: Boolean = false): AppResult<DashboardSnapshot>
}

data class DashboardSnapshot(
    val value: DashboardData,
    val freshness: CacheFreshness,
)
