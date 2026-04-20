package com.wealthvault.`auth-api`.rspassword

import com.wealthvault.`auth-api`.model.ResetPasswordRequest
import com.wealthvault.`auth-api`.model.ResetPasswordResponse
import com.wealthvault.config.Config
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.patch // 🌟 1. เปลี่ยน import จาก post เป็น patch
import io.ktor.client.request.setBody

class ResetApiImpl(private val ktorfit: Ktorfit) : ResetApi {
    override suspend fun reset(request: ResetPasswordRequest): ResetPasswordResponse {
        val client = ktorfit.httpClient

        // 🌟 2. เปลี่ยนจาก client.post เป็น client.patch
        return client.patch("${Config.localhost_android}auth/reset/password") {
            setBody(request)
        }.body()
    }
}