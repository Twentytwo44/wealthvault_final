package com.wealthvault.`user-api`.friendmsg

import com.wealthvault.config.Config
import com.wealthvault.`user-api`.model.MessageResponse
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.get

class GetFriendMsgApiImpl(private val ktorfit: Ktorfit) : GetFriendMsgApi {
    override suspend fun getFriendMsg(id:String): MessageResponse {
        // ใช้ HttpClient ที่อยู่ใน Ktorfit ส่งค่าออกไปจริงๆ
        val client = ktorfit.httpClient

        return client.get("${Config.localhost_android}friend/${id}/msg/") {

        }.body()
    }
}
