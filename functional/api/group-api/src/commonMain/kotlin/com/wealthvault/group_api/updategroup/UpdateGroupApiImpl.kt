package com.wealthvault.group_api.updategroup

import com.wealthvault.config.Config
import com.wealthvault.group_api.model.GroupResponse
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders

class UpdateGroupApiImpl(private val ktorfit: Ktorfit) : UpdateGroupApi {
    override suspend fun updateGroup(id: String, groupName: String, imageBytes: ByteArray?): GroupResponse {
        val client = ktorfit.httpClient
        return client.patch("${Config.localhost_android}group/${id}/") {
            setBody(
                MultiPartFormDataContent(
                    formData {
                        // 🌟 ส่ง group_name ตาม Postman
                        append("group_name", groupName)

                        // 🌟 ส่ง profile_image (ถ้ามีรูปถูกแนบมา)
                        if (imageBytes != null) {
                            append("profile_image", imageBytes, Headers.build {
                                append(HttpHeaders.ContentType, "image/jpeg")
                                append(HttpHeaders.ContentDisposition, "filename=\"profile.jpg\"")
                            })
                        }
                    }
                )
            )
        }.body()
    }
}