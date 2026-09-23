package com.wealthvault.`auth-api`.register

import com.wealthvault.`auth-api`.model.RegisterRequest
import com.wealthvault.auth_api.toDomain
import com.wealthvault.config.Config
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class RegisterApiImpl(private val client: HttpClient) : RegisterApi {
    override suspend fun register(email: String, password: String) =
        client.post("${Config.localhost_android}auth/register") {
            setBody(RegisterRequest(email = email, password = password))
        }.body<com.wealthvault.`auth-api`.model.RegisterResponse>().toDomain()
}
