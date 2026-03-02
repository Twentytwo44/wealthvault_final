package com.wealthvault.login.data

import com.wealthvault.`auth-api`.login.LoginApi
import com.wealthvault.`auth-api`.model.LoginData
import com.wealthvault.`auth-api`.model.LoginRequest

class AuthNetworkDataSource(
    private val loginApi: LoginApi,
) {
    suspend fun login(request: LoginRequest): Result<LoginData> {
        return runCatching {
            val result = loginApi.login(request)
            result.data?: throw IllegalArgumentException("Token is null")
        }
    }
}
