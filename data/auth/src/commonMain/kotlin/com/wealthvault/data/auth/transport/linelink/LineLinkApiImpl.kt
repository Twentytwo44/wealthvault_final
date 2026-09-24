package com.wealthvault.data.auth.transport.linelink

import com.wealthvault.data.auth.transport.model.TokenRequest
import com.wealthvault.data.auth.wire.toDomain
import com.wealthvault.config.Config
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody

internal class LineLinkApiImpl(private val client: HttpClient) : LineLinkApi {
    override suspend fun link(token: String) =
        client.post("${Config.localhost_android}auth/line/link") {
            setBody(TokenRequest(token = token))
        }.body<com.wealthvault.data.auth.transport.model.TokenResponse>().toDomain()
}
