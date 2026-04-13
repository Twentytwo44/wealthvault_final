package com.wealthvault.investment_api.createcash


import com.wealthvault.config.Config
import com.wealthvault.group_api.model.GroupRequest
import com.wealthvault.group_api.model.GroupResponse
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class CreateGroupApiImpl(private val ktorfit: Ktorfit) : CreateGroupApi {
    override suspend fun createGroup(request: GroupRequest): GroupResponse {
        // ใช้ HttpClient ที่อยู่ใน Ktorfit ส่งค่าออกไปจริงๆ
        val client = ktorfit.httpClient

        return client.post("${Config.localhost_android}asset/group/") {
            setBody(request)
        }.body()
    }
}
