package com.wealthvault.group_api.getmember

import com.wealthvault.config.Config
import com.wealthvault.group_api.model.GroupMemberResponse
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.get

class GetGroupMemberApiImpl(private val ktorfit: Ktorfit) : GetGroupMemberApi {
    override suspend fun getAllGroup(id:String): GroupMemberResponse {
        // ใช้ HttpClient ที่อยู่ใน Ktorfit ส่งค่าออกไปจริงๆ
        val client = ktorfit.httpClient

        return client.get("${Config.localhost_android}group/member/${id}/") {

        }.body()
    }
}
