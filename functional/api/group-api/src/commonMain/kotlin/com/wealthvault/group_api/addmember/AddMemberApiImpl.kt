package com.wealthvault.group_api.addmember

import com.wealthvault.config.Config
import com.wealthvault.group_api.model.MemberResponse
import com.wealthvault.group_api.requireSuccess
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData

class AddMemberApiImpl(private val client: HttpClient) : AddMemberApi {
    override suspend fun addMember(id: String, targetId: String): Boolean {
        return client.post("${Config.localhost_android}group/${id}/addmember") {
            setBody(
                MultiPartFormDataContent(
                    formData {
                        // 🌟 ใช้ target_ids (มี s) ตาม Postman
                        append("target_ids", targetId)
                    }
                )
            )
        }.body<MemberResponse>().requireSuccess()
    }
}
