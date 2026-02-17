package com.wealthvault.`auth-api`.rspassword

import com.wealthvault.`auth-api`.model.ResetPasswordRequest
import com.wealthvault.`auth-api`.model.ResetPasswordResponse
import com.wealthvault.config.Config
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class ResetApiImpl(private val ktorfit: Ktorfit) : ResetApi {
    override suspend fun reset(request: ResetPasswordRequest): ResetPasswordResponse {
        // ใช้ HttpClient ที่อยู่ใน Ktorfit ส่งค่าออกไปจริงๆ
        val client = ktorfit.httpClient

        // ยิงเองตรงๆ แบบไม่ง้อ Generator
        return client.post("${Config.localhost_ios}auth/reset/password") {
            setBody(request)
        }.body()
    }
}

