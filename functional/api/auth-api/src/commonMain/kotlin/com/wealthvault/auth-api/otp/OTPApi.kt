package com.wealthvault.`auth-api`.otp

import com.wealthvault.`auth-api`.model.OTPRequest
import com.wealthvault.`auth-api`.model.OTPResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST

interface OTPApi {
    @POST("auth/forgot/otp")
    suspend fun otp(@Body request: OTPRequest): OTPResponse
}
