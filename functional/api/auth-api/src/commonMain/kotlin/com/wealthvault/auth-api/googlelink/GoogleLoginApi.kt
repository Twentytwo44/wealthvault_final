package com.wealthvault.`auth-api`.googlelink

import com.wealthvault.domain.auth.AuthenticatedSession

interface GoogleLoginApi {
    suspend fun glogin(token: String): AuthenticatedSession
}
