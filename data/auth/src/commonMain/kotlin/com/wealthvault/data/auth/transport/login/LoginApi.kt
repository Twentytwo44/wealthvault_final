package com.wealthvault.data.auth.transport.login

import com.wealthvault.domain.auth.AuthenticatedSession

internal interface LoginApi {
    suspend fun login(email: String, password: String): AuthenticatedSession
}
