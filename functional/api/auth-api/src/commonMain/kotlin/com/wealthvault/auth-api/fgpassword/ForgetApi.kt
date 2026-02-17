package com.wealthvault.`auth-api`.fgpassword

import com.wealthvault.`auth-api`.model.ForgetPasswordRequest
import com.wealthvault.`auth-api`.model.ForgetPasswordResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST

interface ForgetApi {
    @POST("auth/forgot/password")
    suspend fun forgetpassword(@Body request: ForgetPasswordRequest): ForgetPasswordResponse
}
