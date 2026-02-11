package com.wealthvault.`auth-api`.login

import com.wealthvault.`auth-api`.model.LoginRequest
import com.wealthvault.`auth-api`.model.LoginResponse
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class LoginApiImpl(private val ktorfit: Ktorfit) : LoginApi {
    override suspend fun login(request: LoginRequest): LoginResponse {
        // ใช้ HttpClient ที่อยู่ใน Ktorfit ส่งค่าออกไปจริงๆ
        return ktorfit.httpClient.post("auth/login") {
            setBody(request)
        }.body()
    }
}
