package com.wealthvault.group_api.removemember

import com.wealthvault.config.Config
import com.wealthvault.group_api.model.MemberResponse
import com.wealthvault.group_api.requireSuccess
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete // 🌟 นำเข้า delete
import io.ktor.client.request.setBody
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData

class RemoveMemberApiImpl(private val client: HttpClient) : RemoveMemberApi {
    override suspend fun removeMember(id: String, targetId: String): Boolean {
        // 🌟 เปลี่ยนจาก post เป็น delete ตาม Postman
        return client.delete("${Config.localhost_android}group/${id}/removemember") {
            setBody(
                MultiPartFormDataContent(
                    formData {
                        // 🌟 ใช้ target_id (ไม่มี s) ตาม Postman
                        append("target_id", targetId)
                    }
                )
            )
        }.body<MemberResponse>().requireSuccess()
    }
}
