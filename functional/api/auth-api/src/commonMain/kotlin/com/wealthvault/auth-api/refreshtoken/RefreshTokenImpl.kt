package com.wealthvault.`auth-api`.refreshtoken


import com.wealthvault.`auth-api`.model.RefreshRequest
import com.wealthvault.auth_api.toDomain
import com.wealthvault.config.Config
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class RefreshTokenImpl(private val client: HttpClient) : RefreshTokenApi {
    override suspend fun refresh(refreshToken: String) =
        client.post("${Config.localhost_ios}auth/refresh") {
            setBody(RefreshRequest(refreshtoken = refreshToken))
        }.body<com.wealthvault.`auth-api`.model.RefreshResponse>().toDomain()
}
