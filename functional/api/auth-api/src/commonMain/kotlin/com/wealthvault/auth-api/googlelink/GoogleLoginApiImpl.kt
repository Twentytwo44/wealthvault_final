package com.wealthvault.`auth-api`.googlelink

import com.wealthvault.`auth-api`.model.LoginResponse
import com.wealthvault.`auth-api`.model.TokenRequest
import com.wealthvault.config.Config
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class GoogleLoginApiImpl(private val ktorfit: Ktorfit) : GoogleLoginApi {
    override suspend fun glogin(request: TokenRequest): LoginResponse {
        // ใช้ HttpClient ที่อยู่ใน Ktorfit ส่งค่าออกไปจริงๆ
        val client = ktorfit.httpClient

        // ยิงเองตรงๆ แบบไม่ง้อ Generator
        return client.post("${Config.localhost_android}auth/login/google") {
            setBody(request)
        }.body()
    }
}
