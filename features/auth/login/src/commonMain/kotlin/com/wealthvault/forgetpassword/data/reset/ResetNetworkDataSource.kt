package com.wealthvault.forgetpassword.data.reset

import com.wealthvault.`auth-api`.model.ResetPasswordRequest
import com.wealthvault.`auth-api`.rspassword.ResetApi

class ResetNetworkDataSource(
    private val resetApi: ResetApi,
) {
    suspend fun forgetPassword(request: ResetPasswordRequest): Result<Boolean> {
        return runCatching {
            val result = resetApi.reset(request)

            // ตรวจสอบว่า success เป็น true จริงๆ ไหม
            if (result.data?.success == true) {
                true // ส่งค่ากลับไปว่าสำเร็จ
            } else {
                throw IllegalArgumentException(result.error ?: "Failed")
            }
        }
    }
}
