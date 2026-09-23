package com.wealthvault.`auth-api`.linelink

import com.wealthvault.`auth-api`.model.TokenRequest
import com.wealthvault.auth_api.toDomain
import com.wealthvault.config.Config
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class LineLinkApiImpl(private val client: HttpClient) : LineLinkApi {
    override suspend fun link(token: String) =
        client.post("${Config.localhost_android}auth/line/link") {
            setBody(TokenRequest(token = token))
        }.body<com.wealthvault.`auth-api`.model.TokenResponse>().toDomain()
}
