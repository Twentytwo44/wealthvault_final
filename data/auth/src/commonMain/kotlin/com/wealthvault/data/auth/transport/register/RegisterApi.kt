package com.wealthvault.data.auth.transport.register

import com.wealthvault.domain.auth.RegistrationResult

internal interface RegisterApi {
    suspend fun register(email: String, password: String): RegistrationResult
}
