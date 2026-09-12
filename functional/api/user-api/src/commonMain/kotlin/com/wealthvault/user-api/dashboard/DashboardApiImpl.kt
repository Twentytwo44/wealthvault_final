package com.wealthvault.`user-api`.dashboard

import com.wealthvault.config.Config
import com.wealthvault.`user-api`.model.DashboardDataResponse
import io.ktor.client.call.body
import io.ktor.client.HttpClient
import io.ktor.client.request.get

class DashboardApiImpl(private val client: HttpClient) : DashboardApi {
    override suspend fun getDashboard(): DashboardDataResponse {
        return client.get("${Config.localhost_android}dashboard") {
            // ใส่ Header หรือ Token ถ้าจำเป็น
        }.body()
    }
}
