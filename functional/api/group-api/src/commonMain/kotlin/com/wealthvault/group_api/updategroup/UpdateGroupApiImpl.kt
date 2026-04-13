package com.wealthvault.group_api.updategroup


import com.wealthvault.config.Config
import com.wealthvault.group_api.model.GroupRequest
import com.wealthvault.group_api.model.GroupResponse
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.patch

class UpdateGroupApiImpl(private val ktorfit: Ktorfit) : UpdateGroupApi {
    override suspend fun updateGroup(id: String, request: GroupRequest): GroupResponse {
        // ใช้ HttpClient ที่อยู่ใน Ktorfit ส่งค่าออกไปจริงๆ
        val client = ktorfit.httpClient

        return client.patch("${Config.localhost_android}group/${id}/") {


        }.body()
    }
}
