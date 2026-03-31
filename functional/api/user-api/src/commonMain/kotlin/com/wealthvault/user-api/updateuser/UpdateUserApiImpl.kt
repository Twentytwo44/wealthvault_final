package com.wealthvault.`user-api`.updateuser

import com.wealthvault.`user-api`.model.UpdateUserDataResponse
import com.wealthvault.config.Config
import com.wealthvault_final.setup_api.HttpClientBuilder
import kotlinx.serialization.json.Json
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import com.wealthvault.data_store.TokenStore
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders

class UpdateUserApiImpl(
    private val ktorfit: Ktorfit,
    private val tokenStore: TokenStore
) : UpdateUserApi {

    override suspend fun updateUser(
        username: String,
        firstName: String,
        lastName: String,
        birthday: String,
        phoneNumber: String,
        profileImage: ByteArray?,
        sharedEnabled: Boolean?, // 🌟 เป็น nullable อยู่แล้ว ดีมากครับ
        sharedAge: Int?         // 🌟 เป็น nullable อยู่แล้ว ดีมากครับ
    ): UpdateUserDataResponse {

        val clientWithAuth = HttpClientBuilder(Json, tokenStore).build(withAuth = true)

        try {
            return clientWithAuth.patch("${Config.localhost_android}user") {
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            // 💡 แนะนำ: ในอนาคตถ้าอยากให้หน้าแก้ไขโปรไฟล์ส่งไป "แค่ฟิลด์ที่แก้" จริงๆ
                            // ต้องไปแก้ Interface ให้พวก String พวกนี้เป็น String? แบบ nullable ด้วยนะครับ
                            append("username", username)
                            append("first_name", firstName)
                            append("last_name", lastName)
                            append("birthday", birthday)
                            append("phonenumber", phoneNumber)

                            if (profileImage != null) {
                                append("profile_image", profileImage, Headers.build {
                                    append(HttpHeaders.ContentType, "image/jpeg")
                                    append(HttpHeaders.ContentDisposition, "filename=\"profile.jpg\"")
                                })
                            }

                            // 🌟 เพิ่มเงื่อนไขเช็ค ส่งไปเฉพาะตอนที่ค่าไม่เป็น null
                            if (sharedEnabled != null) {
                                append("shared_enabled", sharedEnabled.toString())
                            }

                            if (sharedAge != null) {
                                append("shared_age", sharedAge.toString())
                            }
                        }
                    )
                )
            }.body()
        } finally {
            clientWithAuth.close()
        }
    }
}