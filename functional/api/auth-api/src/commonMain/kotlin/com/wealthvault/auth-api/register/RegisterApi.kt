package com.wealthvault.`auth-api`.register

import com.wealthvault.`auth-api`.model.RegisterRequest
import com.wealthvault.`auth-api`.model.RegisterResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST

interface RegisterApi {
    @POST("/auth/register")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse
}
