package com.wealthvault_final.`financial-asset`.data

import com.wealthvault.data_store.TokenStore

class AssetRepositoryImpl(
    private val networkDataSource: AssetNetworkDataSource,
    private val localDataSource: TokenStore // สำหรับเซฟ Token ลงเครื่อง
) {
//    suspend fun login(request: LoginRequest): Result<Unit> {
//        return networkDataSource.login(request).map { data ->
//            // เซฟทั้ง Access Token และ Refresh Token ลงเครื่องพร้อมกัน
//            localDataSource.saveTokens(
//                access = data.accessToken,
//                refresh = data.refreshToken
//            )
//        }
//    }

}
