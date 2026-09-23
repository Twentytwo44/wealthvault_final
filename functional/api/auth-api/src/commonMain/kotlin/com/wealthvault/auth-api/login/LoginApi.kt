package com.wealthvault.`auth-api`.login

import com.wealthvault.domain.auth.AuthenticatedSession

interface LoginApi {
    suspend fun login(email: String, password: String): AuthenticatedSession
}
