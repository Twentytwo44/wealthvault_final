package com.example.google_auth

interface GoogleAuth {
    suspend fun signIn(): GoogleUser?
}

data class GoogleUser(
    val idToken: String,
    val accessToken: String?,
    val email: String?,
    val displayName: String?,
    val photoUrl: String?,
    val userId: String
)
