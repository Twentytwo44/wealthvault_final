package com.wealthvault.`user-api`.dashboard

import com.wealthvault.config.Config
import com.wealthvault.`user-api`.model.DashboardDataResponse
import com.wealthvault.core.model.DashboardData
import com.wealthvault.user_api.toDomain
import io.ktor.client.call.body
import io.ktor.client.HttpClient
import io.ktor.client.request.get

class DashboardApiImpl(private val client: HttpClient) : DashboardApi {
    override suspend fun getDashboard(): DashboardData = client
        .get("${Config.localhost_android}dashboard")
        .body<DashboardDataResponse>()
        .toDomain()
}
