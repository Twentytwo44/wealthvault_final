package com.wealthvault.`user-api`.getuserbyemail

import com.wealthvault.config.Config
import com.wealthvault.`user-api`.model.SearchUserResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import com.wealthvault.user_api.requireDomainUsers

class GetUserByEmailApiImpl(private val client: HttpClient) : GetUserByEmailApi {
    override suspend fun searchUserByEmail(email: String) = client
        .post("${Config.localhost_android}user/search") {
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("email", email)
                    }
                )
            )
        }.body<SearchUserResponse>().requireDomainUsers()
}
