package com.wealthvault.financiallist.data

import com.wealthvault.`auth-api`.model.RegisterRequest
import com.wealthvault.`auth-api`.register.RegisterApi

class FinanciallistDataSource(
    private val registerApi: RegisterApi,
) {
    suspend fun register(request: RegisterRequest): Result<String> {
        return runCatching {
            val result = registerApi.register(request)
            result.data?.userId ?: throw IllegalArgumentException("User is null, Cannot create user")
        }
    }
}

