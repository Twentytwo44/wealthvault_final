package com.wealthvault.data.auth.transport.googlelink

import com.wealthvault.domain.auth.AuthenticatedSession

internal interface GoogleLoginApi {
    suspend fun glogin(token: String): AuthenticatedSession
}
