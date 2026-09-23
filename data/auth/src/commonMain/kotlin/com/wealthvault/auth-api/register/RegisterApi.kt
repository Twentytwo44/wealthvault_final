package com.wealthvault.data.auth.transport.register

import com.wealthvault.domain.auth.RegistrationResult

interface RegisterApi {
    suspend fun register(email: String, password: String): RegistrationResult
}
