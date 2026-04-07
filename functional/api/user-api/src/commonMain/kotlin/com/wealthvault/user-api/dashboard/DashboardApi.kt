package com.wealthvault.`user-api`.dashboard

import com.wealthvault.`user-api`.model.DashboardDataResponse
import de.jensklingenberg.ktorfit.http.GET

interface DashboardApi {
    @GET("dashboard")
    suspend fun getDashboard(): DashboardDataResponse
}