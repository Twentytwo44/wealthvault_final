package com.wealthvault.forgetpassword.data.otp

import com.wealthvault.`auth-api`.model.OTPRequest
import com.wealthvault.`auth-api`.otp.OTPApi

class OTPNetworkDataSource(
    private val otpApi: OTPApi,
) {
    suspend fun otp(request: OTPRequest): Result<String> {
        return runCatching {
            val result = otpApi.otp(request)

            // 🌟 1. ดึงข้อมูลมาเก็บไว้ในตัวแปร Local ก่อน
            val responseData = result.data

            // 🌟 2. เอาตัวแปร Local มาเช็คและใช้งาน (จะไม่ติดแดงแล้ว!)
            if (responseData?.success == true) {
                responseData.resetToken // ส่ง Token กลับไป
            } else {
                throw IllegalArgumentException(result.error ?: "OTP failed")
            }
        }
    }
}