package com.wealthvault.group_api.addmember

import com.wealthvault.config.Config
import com.wealthvault.group_api.model.MemberResponse
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData

class AddMemberApiImpl(private val ktorfit: Ktorfit) : AddMemberApi {
    override suspend fun addMember(id: String, targetId: String): MemberResponse {
        val client = ktorfit.httpClient
        return client.post("${Config.localhost_android}group/${id}/addmember") {
            setBody(
                MultiPartFormDataContent(
                    formData {
                        // 🌟 ใช้ target_ids (มี s) ตาม Postman
                        append("target_ids", targetId)
                    }
                )
            )
        }.body()
    }
}