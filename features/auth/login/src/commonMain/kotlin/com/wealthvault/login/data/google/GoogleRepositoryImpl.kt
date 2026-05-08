package com.wealthvault.login.data.google

import com.wealthvault.`auth-api`.model.LoginData
import com.wealthvault.`auth-api`.model.TokenRequest
import com.wealthvault.data_store.AuthToken
import com.wealthvault.data_store.TokenStore
import com.wealthvault.data_store.UserId

class GoogleRepositoryImpl(
    private val networkDataSource: GoogleNetworkDataSource,
    private val localDataSource: TokenStore // สำหรับเซฟ Token ลงเครื่อง
) {
    suspend fun glogin(request: TokenRequest): Result<LoginData> {
        return networkDataSource.googleLogin(request).map { data ->
            // เซฟทั้ง Access Token และ Refresh Token ลงเครื่องพร้อมกัน
            println("GoogleLoginResponse ${data}")
            val tokens = AuthToken(data.accessToken,data.refreshToken)
            localDataSource.saveAuthToken(tokens)
            val userId = UserId(data.userId)
            localDataSource.saveUserId(userId)
            data

        }
    }

}
