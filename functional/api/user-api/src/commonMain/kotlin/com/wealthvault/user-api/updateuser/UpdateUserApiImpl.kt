package com.wealthvault.`user-api`.updateuser

import com.wealthvault.config.Config
import com.wealthvault.domain.profile.UpdateUserData
import com.wealthvault.domain.profile.UpdateUserDataRequest
import com.wealthvault.user_api.requireDomainUpdatedUser
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders

class UpdateUserApiImpl(
    private val client: HttpClient,
) : UpdateUserApi {

    override suspend fun updateUser(
       request: UpdateUserDataRequest
    ): UpdateUserData {

        return client.patch("${Config.localhost_android}user") {
            setBody(
                MultiPartFormDataContent(
                    formData {
                        // 💡 แนะนำ: ในอนาคตถ้าอยากให้หน้าแก้ไขโปรไฟล์ส่งไป "แค่ฟิลด์ที่แก้" จริงๆ
                        // ต้องไปแก้ Interface ให้พวก String พวกนี้เป็น String? แบบ nullable ด้วยนะครับ
                        append("username", request.username)
                        append("first_name", request.firstName)
                        append("last_name", request.lastName)
                        append("birthday", request.birthday)
                        append("phonenumber", request.phoneNumber)

                        request.profileImage?.let { imageBytes ->
                            append("profile_image", imageBytes, Headers.build {
                                append(HttpHeaders.ContentType, "image/jpeg")
                                append(HttpHeaders.ContentDisposition, "filename=\"profile.jpg\"")
                            })
                        }

                        // 🌟 เพิ่มเงื่อนไขเช็ค ส่งไปเฉพาะตอนที่ค่าไม่เป็น null
                        if (request.sharedEnabled != null) {
                            append("shared_enabled", request.sharedEnabled.toString())
                        }

                        if (request.sharedAge != null) {
                            append("shared_age", request.sharedAge.toString())
                        }
                    }
                )
            )
        }.body<com.wealthvault.`user-api`.model.UpdateUserDataResponse>().requireDomainUpdatedUser()
    }
}
