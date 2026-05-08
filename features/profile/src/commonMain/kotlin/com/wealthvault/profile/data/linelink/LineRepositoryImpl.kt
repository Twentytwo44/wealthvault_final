
import com.wealthvault.`auth-api`.model.TokenRequest
import com.wealthvault.`auth-api`.model.TokenResponse
import com.wealthvault.profile.data.linelink.LineNetworkDataSource

//package com.wealthvault.profile.data.linelink

class LineRepositoryImpl(
    private val networkDataSource: LineNetworkDataSource,
) {
    suspend fun link(request: TokenRequest): Result<TokenResponse> {
        return networkDataSource.lineLogin(request).map { data ->
            // เซฟทั้ง Access Token และ Refresh Token ลงเครื่องพร้อมกัน
            println("LineLoginResponse ${data}")
            data

        }
    }

}
