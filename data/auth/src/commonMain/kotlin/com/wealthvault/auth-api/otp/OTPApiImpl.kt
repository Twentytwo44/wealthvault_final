package com.wealthvault.data.auth.transport.otp


import com.wealthvault.data.auth.transport.model.OTPRequest
import com.wealthvault.data.auth.wire.toDomain
import com.wealthvault.config.Config
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class OTPApiImpl(private val client: HttpClient) : OTPApi {
    override suspend fun otp(email: String, otp: String) =
        client.post("${Config.localhost_android}auth/forgot/otp") {
            setBody(OTPRequest(email = email, otp = otp))
        }.body<com.wealthvault.data.auth.transport.model.OTPResponse>().toDomain()
}
