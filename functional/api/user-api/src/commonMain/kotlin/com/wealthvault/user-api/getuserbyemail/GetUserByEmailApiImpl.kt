package com.wealthvault.`user-api`.getuserbyemail

import com.wealthvault.config.Config
import com.wealthvault.`user-api`.model.SearchUserResponse
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class GetUserByEmailApiImpl(private val ktorfit: Ktorfit) : GetUserByEmailApi {
    override suspend fun searchUserByEmail(email: String): SearchUserResponse {
        val client = ktorfit.httpClient

        // 🌟 ยิง API ไปที่ /user/search ด้วยรูปแบบ Form-data ให้ตรงกับ Postman
        return client.post("${Config.localhost_android}user/search") {
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("email", email)
                    }
                )
            )
        }.body()
    }
}