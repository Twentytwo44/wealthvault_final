package com.wealthvault.`auth-api`.register

import com.wealthvault.`auth-api`.model.RegisterRequest
import com.wealthvault.`auth-api`.model.RegisterResponse
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class RegisterApiImpl(private val ktorfit: Ktorfit) : RegisterApi {
    override suspend fun register(request: RegisterRequest): RegisterResponse {
        // ใช้ HttpClient ที่อยู่ใน Ktorfit ส่งค่าออกไปจริงๆ
        return ktorfit.httpClient.post("auth/register") {
            setBody(request)
        }.body()
    }
}

