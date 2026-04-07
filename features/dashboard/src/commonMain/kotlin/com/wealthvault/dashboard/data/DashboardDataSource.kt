package com.wealthvault.dashboard.data

import com.wealthvault.`user-api`.dashboard.DashboardApi
import com.wealthvault.`user-api`.model.DashboardDataResponse

class DashboardDataSource(
    private val dashboardApi: DashboardApi, // 🌟 แก้เป็นตัวเล็ก (camelCase)
) {
    suspend fun fetchDashboardData(): Result<DashboardDataResponse> {
        return runCatching {
            dashboardApi.getDashboard()
        }
    }
}