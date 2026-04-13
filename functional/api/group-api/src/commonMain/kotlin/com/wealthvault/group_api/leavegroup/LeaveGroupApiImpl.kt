package com.wealthvault.group_api.leavegroup


import com.wealthvault.config.Config
import com.wealthvault.group_api.model.MemberResponse
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.post

class LeaveGroupApiImpl(private val ktorfit: Ktorfit) : LeaveGroupAccessApi {
    override suspend fun leaveGroup(id: String): MemberResponse {
        // ใช้ HttpClient ที่อยู่ใน Ktorfit ส่งค่าออกไปจริงๆ
        val client = ktorfit.httpClient

        return client.post("${Config.localhost_android}group/${id}/leave/") {

        }.body()
    }
}
