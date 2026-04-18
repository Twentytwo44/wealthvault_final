package com.wealthvault.group_api.creategroup

import com.wealthvault.config.Config
import com.wealthvault.group_api.model.GroupResponse
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders

class CreateGroupApiImpl(private val ktorfit: Ktorfit) : CreateGroupApi {
    override suspend fun createGroup(
        groupName: String,
        memberIds: List<String>,
        imageBytes: ByteArray?
    ): GroupResponse {
        val client = ktorfit.httpClient

        // 🌟 เช็ค Endpoint ให้ชัวร์นะครับ (ใน Postman คือ /api/group)
        return client.post("${Config.localhost_android}group/") {
            setBody(
                MultiPartFormDataContent(
                    formData {
                        // 1. แนบชื่อกลุ่ม
                        append("group_name", groupName)

                        // 2. แนบสมาชิก (วนลูปแนบ member_ids ทีละคนเหมือนใน Postman)
                        memberIds.forEach { id ->
                            append("member_ids", id)
                        }

                        // 3. แนบไฟล์รูปภาพ (ถ้ามี)
                        if (imageBytes != null) {
                            append("profile_image", imageBytes, Headers.build {
                                append(HttpHeaders.ContentType, "image/png")
                                append(HttpHeaders.ContentDisposition, "filename=\"group_profile.png\"")
                            })
                        }
                    }
                )
            )
        }.body()
    }
}