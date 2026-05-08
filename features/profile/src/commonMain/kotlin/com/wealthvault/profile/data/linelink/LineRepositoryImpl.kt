//package com.wealthvault.profile.data.linelink

//class LineRepositoryImpl(
//    private val networkDataSource: LineNetworkDataSource,
//    private val localDataSource: TokenStore // สำหรับเซฟ Token ลงเครื่อง
//) {
//    suspend fun link(request: TokenRequest): Result<LoginData> {
//        return networkDataSource.googleLogin(request).map { data ->
//            // เซฟทั้ง Access Token และ Refresh Token ลงเครื่องพร้อมกัน
//            println("GoogleLoginResponse ${data}")
//            val tokens = AuthToken(data.accessToken,data.refreshToken)
//            localDataSource.saveAuthToken(tokens)
//            val userId = UserId(data.userId)
//            localDataSource.saveUserId(userId)
//            data
//
//        }
//    }
//
//}
