//package com.wealthvault.profile.data.linelink

import com.wealthvault.`auth-api`.googlelink.GoogleLoginApi
import com.wealthvault.`auth-api`.linelink.LineLinkApi
import com.wealthvault.`auth-api`.model.LoginData
import com.wealthvault.`auth-api`.model.TokenRequest

//class LineNetworkDataSource(
//    private val lineLinkApi: LineLinkApi
//) {
//    suspend fun googleLogin(request: TokenRequest): Result<LoginData> {
//        return runCatching {
//            val result = lineLinkApi.link(request)
//
//            // 🌟 1. ให้มันหันไปมอง Error ก่อน!
//            // ถ้า Backend พ่น Error กลับมา (เช่น รหัสผิด) ให้โยนข้อความนั้นออกไปเลย
//            if (result.error != null) {
//                throw Exception(result.error)
//            }
//
//            // 🌟 2. ถ้าผ่านด่าน Error มาได้ (ล็อกอินสำเร็จ) ค่อยมาดึง Data
//            result.data ?: throw IllegalArgumentException("Token is null")
//        }
//    }
}
