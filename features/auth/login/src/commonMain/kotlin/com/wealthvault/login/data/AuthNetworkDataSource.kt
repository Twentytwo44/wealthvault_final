package com.wealthvault.login.data

import com.wealthvault.`auth-api`.login.LoginApi
import com.wealthvault.`auth-api`.model.LoginRequest

//class AuthNetworkDataSource(
//    private val loginApi: LoginApi,
//) {
//    suspend fun login(request: LoginRequest): Result<String> {
//        return runCatching {
//            val result = loginApi.login(request)
//            result.data?.accessToken ?: throw IllegalArgumentException("Token is null")
//        }
//    }
//}
