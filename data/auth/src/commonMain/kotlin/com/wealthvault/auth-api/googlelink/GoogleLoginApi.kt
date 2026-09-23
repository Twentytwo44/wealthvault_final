package com.wealthvault.data.auth.transport.googlelink

import com.wealthvault.domain.auth.AuthenticatedSession

interface GoogleLoginApi {
    suspend fun glogin(token: String): AuthenticatedSession
}
