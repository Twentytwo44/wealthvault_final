package com.wealthvault.`auth-api`.googlelink

import com.wealthvault.`auth-api`.model.TokenRequest
import com.wealthvault.auth_api.toDomain
import com.wealthvault.config.Config
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class GoogleLoginApiImpl(private val client: HttpClient) : GoogleLoginApi {
    override suspend fun glogin(token: String) =
        client.post("${Config.localhost_android}auth/login/google") {
            setBody(TokenRequest(token = token))
        }.body<com.wealthvault.`auth-api`.model.LoginResponse>().toDomain()
}
