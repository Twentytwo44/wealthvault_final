package com.wealthvault.data.auth.transport.login

import com.wealthvault.domain.auth.AuthenticatedSession

interface LoginApi {
    suspend fun login(email: String, password: String): AuthenticatedSession
}
