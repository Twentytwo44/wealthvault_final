package com.wealthvault.`auth-api`.linelink

import com.wealthvault.`auth-api`.model.TokenRequest
import com.wealthvault.`auth-api`.model.TokenResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST

interface LineLinkApi {
    @POST("auth/line/link")
    suspend fun link(@Body request: TokenRequest): TokenResponse
}
