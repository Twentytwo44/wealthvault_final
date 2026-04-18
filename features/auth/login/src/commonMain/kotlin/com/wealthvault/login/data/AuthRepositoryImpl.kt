package com.wealthvault.login.data

import com.wealthvault.`auth-api`.model.LoginData
import com.wealthvault.`auth-api`.model.LoginRequest
import com.wealthvault.data_store.AuthToken
import com.wealthvault.data_store.TokenStore

class AuthRepositoryImpl(
    private val networkDataSource: AuthNetworkDataSource,
    private val localDataSource: TokenStore // สำหรับเซฟ Token ลงเครื่อง
) {
    suspend fun login(request: LoginRequest): Result<LoginData> {
        return networkDataSource.login(request).map { data ->
            // เซฟทั้ง Access Token และ Refresh Token ลงเครื่องพร้อมกัน
            println("LoginResponse ${data}")
            val tokens = AuthToken(data.accessToken,data.refreshToken)
            localDataSource.saveAuthToken(tokens)
            data

        }
    }

}
