package com.wealthvault.forgetpassword.data.otp

import com.wealthvault.`auth-api`.model.OTPRequest

class OTPRepositoryImpl(
    private val networkDataSource: OTPNetworkDataSource,
) {
    // 🌟 1. เปลี่ยนจาก Result<Unit> เป็น Result<String>
    suspend fun otp(request: OTPRequest): Result<String> {
        return networkDataSource.otp(request).map { token ->
            println("[OTPRepositoryImpl] OTP Success")
            token // 🌟 2. ส่งตัวแปร token กลับออกไปให้ UseCase (จากเดิมที่ไม่มีบรรทัดนี้)
        }
    }
}