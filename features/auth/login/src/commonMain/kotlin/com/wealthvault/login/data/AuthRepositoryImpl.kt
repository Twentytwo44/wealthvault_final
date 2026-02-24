package com.wealthvault.login.data

import com.wealthvault.`auth-api`.model.LoginRequest
import com.wealthvault.data_store.TokenStore

class AuthRepositoryImpl(
    private val networkDataSource: AuthNetworkDataSource,
    private val localDataSource: TokenStore // สำหรับเซฟ Token ลงเครื่อง
) {
    suspend fun login(request: LoginRequest): Result<Unit> {
        return networkDataSource.login(request).map { data ->
            // เซฟทั้ง Access Token และ Refresh Token ลงเครื่องพร้อมกัน
            localDataSource.saveTokens(
                access = data.accessToken,
                refresh = data.refreshToken
            )
        }
    }

}
