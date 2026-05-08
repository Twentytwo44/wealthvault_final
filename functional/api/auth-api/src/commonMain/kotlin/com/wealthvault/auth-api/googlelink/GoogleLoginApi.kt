package com.wealthvault.`auth-api`.googlelink

import com.wealthvault.`auth-api`.model.LoginResponse
import com.wealthvault.`auth-api`.model.TokenRequest
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST

interface GoogleLoginApi {
    @POST("/auth/login/google")
    suspend fun glogin(@Body request: TokenRequest): LoginResponse
}
