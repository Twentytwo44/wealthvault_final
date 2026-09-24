package com.wealthvault.data.auth.transport.googlelink

import com.wealthvault.data.auth.transport.model.TokenRequest
import com.wealthvault.data.auth.wire.toDomain
import com.wealthvault.config.Config
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody

internal class GoogleLoginApiImpl(private val client: HttpClient) : GoogleLoginApi {
    override suspend fun glogin(token: String) =
        client.post("${Config.localhost_android}auth/login/google") {
            setBody(TokenRequest(token = token))
        }.body<com.wealthvault.data.auth.transport.model.LoginResponse>().toDomain()
}
