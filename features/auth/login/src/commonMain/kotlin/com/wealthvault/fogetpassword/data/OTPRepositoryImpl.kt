package com.wealthvault.fogetpassword.data

import com.wealthvault.`auth-api`.model.OTPRequest

class OTPRepositoryImpl(
    private val networkDataSource: OTPNetworkDataSource,
) {
    suspend fun otp(request: OTPRequest): Result<Unit> {
        return networkDataSource.otp(request).map { token ->
            println("[OTPRepositoryImpl] OTP Succes")
        }
    }

    // สร้าง Flow เพื่อตรวจสอบสถานะล็อกอิน (เลียนแบบ Flow ใน UseCase เดิม)

}
