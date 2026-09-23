package com.wealthvault.`auth-api`.rspassword

import com.wealthvault.`auth-api`.model.ResetPasswordRequest
import com.wealthvault.auth_api.toDomain
import com.wealthvault.config.Config
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.patch // 🌟 1. เปลี่ยน import จาก post เป็น patch
import io.ktor.client.request.setBody

class ResetApiImpl(private val client: HttpClient) : ResetApi {
    override suspend fun reset(resetToken: String, password: String) =
        client.patch("${Config.localhost_android}auth/reset/password") {
            setBody(ResetPasswordRequest(resettoken = resetToken, password = password))
        }.body<com.wealthvault.`auth-api`.model.ResetPasswordResponse>().toDomain()
}
