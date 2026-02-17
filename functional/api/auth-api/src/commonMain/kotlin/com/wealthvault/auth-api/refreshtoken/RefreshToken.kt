package com.wealthvault.`auth-api`.refreshtoken

import com.wealthvault.`auth-api`.model.RefreshRequest
import com.wealthvault.`auth-api`.model.RefreshResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST

interface RefreshTokenApi {
    @POST("auth/refresh")
    suspend fun refresh(@Body request: RefreshRequest): RefreshResponse
}
