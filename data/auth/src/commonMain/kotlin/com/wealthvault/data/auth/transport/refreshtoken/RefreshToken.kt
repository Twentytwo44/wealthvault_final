package com.wealthvault.data.auth.transport.refreshtoken

import com.wealthvault.domain.auth.AuthenticatedSession

internal interface RefreshTokenApi {
    suspend fun refresh(refreshToken: String): AuthenticatedSession
}
