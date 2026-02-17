package com.wealthvault.`auth-api`.otp


import com.wealthvault.`auth-api`.model.OTPRequest
import com.wealthvault.`auth-api`.model.OTPResponse
import com.wealthvault.config.Config
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class OTPApiImpl(private val ktorfit: Ktorfit) : OTPApi {
    override suspend fun otp(request: OTPRequest): OTPResponse{

        val client = ktorfit.httpClient

        return client.post("${Config.localhost_ios}auth/forgot/otp") {
            setBody(request)
        }.body()
    }
}
