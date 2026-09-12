package com.wealthvault.dashboard.data

import com.wealthvault.`user-api`.model.DashboardDataResponse

interface DashboardRepository {
    suspend fun getDashboardData(): Result<DashboardDataResponse>
}
