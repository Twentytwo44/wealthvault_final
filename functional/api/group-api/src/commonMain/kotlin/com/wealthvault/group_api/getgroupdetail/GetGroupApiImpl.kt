package com.wealthvault.group_api.getgroupdetail

import com.wealthvault.config.Config
import com.wealthvault.group_api.model.GroupResponse
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.get

class GetGroupApiImpl(private val ktorfit: Ktorfit) : GetGroupApi {
    override suspend fun getInvestment(id: String): GroupResponse {
        // ใช้ HttpClient ที่อยู่ใน Ktorfit ส่งค่าออกไปจริงๆ
        val client = ktorfit.httpClient

        return client.get("${Config.localhost_android}/group/${id}") {

        }.body()
    }
}
