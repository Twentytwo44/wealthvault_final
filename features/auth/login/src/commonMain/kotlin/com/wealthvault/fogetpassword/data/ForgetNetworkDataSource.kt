package com.wealthvault.fogetpassword.data

import com.wealthvault.`auth-api`.fgpassword.ForgetApi
import com.wealthvault.`auth-api`.model.ForgetPasswordRequest

class ForgetNetworkDataSource(
    private val forgetApi: ForgetApi,
) {
    suspend fun forgetPassword(request: ForgetPasswordRequest): Result<Boolean> {
        return runCatching {
            val result = forgetApi.forgetpassword(request)

            // ตรวจสอบว่า success เป็น true จริงๆ ไหม
            if (result.data?.success == true) {
                true // ส่งค่ากลับไปว่าสำเร็จ
            } else {
                // ถ้าเป็น false หรือเป็น null ให้โยน Error ออกไป
                throw IllegalArgumentException(result.error ?: "Forget password failed")
            }
        }
    }
}
