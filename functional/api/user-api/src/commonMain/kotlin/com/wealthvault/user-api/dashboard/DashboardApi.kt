package com.wealthvault.`user-api`.dashboard

import com.wealthvault.core.model.DashboardData

interface DashboardApi {
    suspend fun getDashboard(): DashboardData
}
