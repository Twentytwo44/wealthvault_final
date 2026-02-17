package com.wealthvault.`auth-api`.fgpassword

import com.wealthvault.`auth-api`.model.ForgetPasswordRequest
import com.wealthvault.`auth-api`.model.ForgetPasswordResponse
import com.wealthvault.config.Config
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class ForgetApiImpl(private val ktorfit: Ktorfit) : ForgetApi {
    override suspend fun forgetpassword(request: ForgetPasswordRequest): ForgetPasswordResponse {
        // ใช้ HttpClient ที่อยู่ใน Ktorfit ส่งค่าออกไปจริงๆ
        val client = ktorfit.httpClient

        // ยิงเองตรงๆ แบบไม่ง้อ Generator
        return client.post("${Config.localhost_ios}auth/login") {
            setBody(request)
        }.body()
    }
}
