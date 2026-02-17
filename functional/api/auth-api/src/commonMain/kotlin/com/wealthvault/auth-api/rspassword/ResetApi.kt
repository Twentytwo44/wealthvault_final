package com.wealthvault.`auth-api`.rspassword

import com.wealthvault.`auth-api`.model.ResetPasswordRequest
import com.wealthvault.`auth-api`.model.ResetPasswordResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.PATCH

interface ResetApi {
    @PATCH("auth/reset/password")
    suspend fun reset(@Body request: ResetPasswordRequest): ResetPasswordResponse
}
