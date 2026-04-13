package com.wealthvault.group_api.groupmsg

import com.wealthvault.config.Config
import com.wealthvault.group_api.model.GroupMsgResponse
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.get

class GetGroupMsgApiImpl(private val ktorfit: Ktorfit) : GetGroupMsgApi {
    override suspend fun getGroupMsg(id:String): GroupMsgResponse {
        // ใช้ HttpClient ที่อยู่ใน Ktorfit ส่งค่าออกไปจริงๆ
        val client = ktorfit.httpClient

        return client.get("${Config.localhost_android}/group/${id}/msg/") {

        }.body()
    }
}
