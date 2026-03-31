package com.wealthvault.dashboard.data

import com.wealthvault.`user-api`.model.DashboardDataResponse

class DashboardRepositoryImpl(
    private val networkDataSource: DashboardDataSource,
) {
    suspend fun getDashboardData(): Result<DashboardDataResponse> {
        return networkDataSource.fetchDashboardData().onSuccess {
            println("✅ ดึงข้อมูล Dashboard สำเร็จ! ทรัพย์สินรวม: ${it.netWorth?.totalAssets}")
        }.onFailure { error ->
            println("🚨 ดึงข้อมูล Dashboard ล้มเหลว: ${error.message}")
        }
    }
}