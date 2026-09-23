package com.wealthvault.`auth-api`.fgpassword

import com.wealthvault.`auth-api`.model.ForgetPasswordRequest
import com.wealthvault.auth_api.toDomain
import com.wealthvault.config.Config
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class ForgetApiImpl(private val client: HttpClient) : ForgetApi {
    override suspend fun forgetpassword(email: String) =
        client.post("${Config.localhost_android}auth/forgot/password") {
            setBody(ForgetPasswordRequest(email = email))
        }.body<com.wealthvault.`auth-api`.model.ForgetPasswordResponse>().toDomain()
}
