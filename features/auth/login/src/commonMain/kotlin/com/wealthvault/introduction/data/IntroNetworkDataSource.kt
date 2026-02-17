package com.wealthvault.introduction.data

import com.wealthvault.`auth-api`.login.LoginApi
import com.wealthvault.`auth-api`.model.LoginRequest

//class IntroNetworkDataSource(
//    private val introApi: IntroApi,
//) {
//    suspend fun login(request: LoginRequest): Result<String> {
//        return runCatching {
//            val result = loginApi.login(request)
//            result.data?.accessToken ?: throw IllegalArgumentException("Token is null")
//        }
//    }
//}
