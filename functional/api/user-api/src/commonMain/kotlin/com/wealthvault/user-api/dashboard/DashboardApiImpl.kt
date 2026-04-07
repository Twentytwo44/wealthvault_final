package com.wealthvault.`user-api`.dashboard

import com.wealthvault.config.Config
import com.wealthvault.`user-api`.model.DashboardDataResponse
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.get

class DashboardApiImpl(private val ktorfit: Ktorfit) : DashboardApi {
    override suspend fun getDashboard(): DashboardDataResponse {
        val client = ktorfit.httpClient

        return client.get("${Config.localhost_android}dashboard") {
            // ใส่ Header หรือ Token ถ้าจำเป็น
        }.body()
    }
}