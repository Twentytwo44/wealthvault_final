package com.wealthvault.group_api.getgrouplist

import com.wealthvault.config.Config
import com.wealthvault.group_api.model.GetGroupResponse
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.get

class GetAllGroupApiImpl(private val ktorfit: Ktorfit) : GetAllGroupApi {
    override suspend fun getAllGroup(): GetGroupResponse {
        // ใช้ HttpClient ที่อยู่ใน Ktorfit ส่งค่าออกไปจริงๆ
        val client = ktorfit.httpClient

        return client.get("${Config.localhost_android}group/") {

        }.body()
    }
}
