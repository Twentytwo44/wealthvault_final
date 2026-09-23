package com.wealthvault.`auth-api`.otp


import com.wealthvault.`auth-api`.model.OTPRequest
import com.wealthvault.auth_api.toDomain
import com.wealthvault.config.Config
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class OTPApiImpl(private val client: HttpClient) : OTPApi {
    override suspend fun otp(email: String, otp: String) =
        client.post("${Config.localhost_android}auth/forgot/otp") {
            setBody(OTPRequest(email = email, otp = otp))
        }.body<com.wealthvault.`auth-api`.model.OTPResponse>().toDomain()
}
