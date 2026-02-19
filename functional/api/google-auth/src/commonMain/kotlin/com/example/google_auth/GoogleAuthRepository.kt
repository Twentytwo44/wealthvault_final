package com.example.google_auth

class GoogleAuthRepository(
    private val googleAuth: GoogleAuth
) {
    suspend fun login(): GoogleUser? {
        return googleAuth.signIn()
    }
}
