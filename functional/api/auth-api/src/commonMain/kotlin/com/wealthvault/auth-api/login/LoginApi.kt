package com.wealthvault.`auth-api`.login

import com.wealthvault.`auth-api`.model.LoginRequest
import com.wealthvault.`auth-api`.model.LoginResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST

interface LoginApi {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse
}
