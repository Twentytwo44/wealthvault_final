package com.wealthvault.data.auth.google

import com.wealthvault.domain.auth.GoogleIdentity

internal class GoogleAuthRepository(
    private val googleAuth: GoogleAuth
) {
    suspend fun login(): GoogleIdentity? {
        return googleAuth.signIn()
    }
}
