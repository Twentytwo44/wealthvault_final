package com.wealthvault_final.`financial-asset`.data

import com.wealthvault.`auth-api`.login.LoginApi

class AssetNetworkDataSource(
    private val loginApi: LoginApi,
) {
//    suspend fun login(request: LoginRequest): Result<LoginData> {
//        return runCatching {
//            val result = loginApi.login(request)
//            result.data?: throw IllegalArgumentException("Token is null")
//        }
//    }
}
