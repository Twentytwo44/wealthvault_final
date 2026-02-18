package com.wealthvault.fogetpassword.data

import com.wealthvault.`auth-api`.model.OTPRequest
import com.wealthvault.`auth-api`.otp.OTPApi

class OTPNetworkDataSource(
    private val otpApi: OTPApi,
) {
    suspend fun otp(request: OTPRequest): Result<Boolean> {
        return runCatching {
            val result = otpApi.otp(request)

            // ตรวจสอบว่า success เป็น true จริงๆ ไหม
            if (result.data?.success == true) {
                true // ส่งค่ากลับไปว่าสำเร็จ
            } else {
                // ถ้าเป็น false หรือเป็น null ให้โยน Error ออกไป
                throw IllegalArgumentException(result.error ?: "OTP failed")
            }
        }
    }
}
