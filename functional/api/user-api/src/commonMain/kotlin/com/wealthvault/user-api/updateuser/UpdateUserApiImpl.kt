package com.wealthvault.`user-api`.updateuser

import com.wealthvault.`user-api`.model.UpdateUserDataResponse
import com.wealthvault.config.Config
// 🌟 1. อย่าลืม Import 2 ตัวนี้นะครับ
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
    private val tokenStore: TokenStore // 🌟 2. ขอรับ TokenStore เข้ามาด้วย
) : UpdateUserApi {

    override suspend fun updateUser(
        username: String,
        firstName: String,
        lastName: String,
        birthday: String,
        phoneNumber: String,
        profileImage: ByteArray?
    ): UpdateUserDataResponse {

        val clientWithAuth = HttpClientBuilder(Json, tokenStore).build(withAuth = true)

        try {
            // ยิง PATCH ด้วย Client ตัวใหม่
            return clientWithAuth.patch("${Config.localhost_android}user") {
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append("username", username)
                            append("first_name", firstName)
                            append("last_name", lastName)
                            append("birthday", birthday)
                            append("phonenumber", phoneNumber)
                            if (profileImage != null) {
                                append("profile_image", profileImage, Headers.build {
                                    append(HttpHeaders.ContentType, "image/jpeg")
                                    // 🌟 ใส่แค่ filename อย่างเดียวพอครับ Ktor จะจัดการที่เหลือให้เอง
                                    append(HttpHeaders.ContentDisposition, "filename=\"profile.jpg\"")
                                })
                            }
                        }
                    )
                )
            }.body()
        } finally {
            // 🌟 4. ยิงเสร็จ อย่าลืมปิด Client ด้วยนะครับ คืนทรัพยากรให้ระบบ
            clientWithAuth.close()
        }
    }
}