package com.wealthvault.data.auth.transport.register

import com.wealthvault.data.auth.transport.model.RegisterRequest
import com.wealthvault.data.auth.wire.toDomain
import com.wealthvault.config.Config
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class RegisterApiImpl(private val client: HttpClient) : RegisterApi {
    override suspend fun register(email: String, password: String) =
        client.post("${Config.localhost_android}auth/register") {
            setBody(RegisterRequest(email = email, password = password))
        }.body<com.wealthvault.data.auth.transport.model.RegisterResponse>().toDomain()
}
