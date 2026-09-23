package com.wealthvault.`auth-api`.refreshtoken

import com.wealthvault.domain.auth.AuthenticatedSession

interface RefreshTokenApi {
    suspend fun refresh(refreshToken: String): AuthenticatedSession
}
