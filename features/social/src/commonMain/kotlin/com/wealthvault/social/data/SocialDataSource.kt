package com.wealthvault.social.data

import com.wealthvault.`auth-api`.model.RegisterRequest
import com.wealthvault.`auth-api`.register.RegisterApi

class SocialDataSource(
    private val registerApi: RegisterApi,
) {
    suspend fun register(request: RegisterRequest): Result<String> {
        return runCatching {
            val result = registerApi.register(request)
            result.data?.userId ?: throw IllegalArgumentException("User is null, Cannot create user")
        }
    }
}

