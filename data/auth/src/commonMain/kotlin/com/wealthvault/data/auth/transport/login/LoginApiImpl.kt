package com.wealthvault.data.auth.transport.login

import com.wealthvault.data.auth.transport.model.LoginRequest
import com.wealthvault.data.auth.wire.toDomain
import com.wealthvault.config.Config
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody

internal class LoginApiImpl(private val client: HttpClient) : LoginApi {
    override suspend fun login(email: String, password: String) =
        client.post("${Config.localhost_android}auth/login") {
            setBody(LoginRequest(email = email, password = password))
        }.body<com.wealthvault.data.auth.transport.model.LoginResponse>().toDomain()
}
