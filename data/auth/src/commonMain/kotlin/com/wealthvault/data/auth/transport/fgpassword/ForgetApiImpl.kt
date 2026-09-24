package com.wealthvault.data.auth.transport.fgpassword

import com.wealthvault.data.auth.transport.model.ForgetPasswordRequest
import com.wealthvault.data.auth.wire.toDomain
import com.wealthvault.config.Config
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody

internal class ForgetApiImpl(private val client: HttpClient) : ForgetApi {
    override suspend fun forgetpassword(email: String) =
        client.post("${Config.localhost_android}auth/forgot/password") {
            setBody(ForgetPasswordRequest(email = email))
        }.body<com.wealthvault.data.auth.transport.model.ForgetPasswordResponse>().toDomain()
}
