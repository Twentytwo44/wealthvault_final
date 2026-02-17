package com.wealthvault.introduction.data

import com.wealthvault.`auth-api`.model.LoginRequest
import com.wealthvault.data_store.TokenStore
import com.wealthvault.login.data.AuthNetworkDataSource

class IntroRepositoryImpl(
    private val networkDataSource: AuthNetworkDataSource,
    private val localDataSource: TokenStore // สำหรับเซฟ Token ลงเครื่อง
) {
    suspend fun login(request: LoginRequest): Result<Unit> {
        return networkDataSource.login(request).map { token ->
            localDataSource.saveToken(token) // บันทึกเก็บไว้ใช้ตรวจสอบสถานะ
        }
    }

    // สร้าง Flow เพื่อตรวจสอบสถานะล็อกอิน (เลียนแบบ Flow ใน UseCase เดิม)

}
