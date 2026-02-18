package com.wealthvault.fogetpassword.data

import com.wealthvault.`auth-api`.model.ForgetPasswordRequest

class ForgetRepositoryImpl(
    private val networkDataSource: ForgetNetworkDataSource,
) {
    suspend fun forgetpassword(request: ForgetPasswordRequest): Result<Unit> {
        return networkDataSource.forgetPassword(request).map { token ->
            println("[ForgetRepositoryImpl] Send OTP Succes")
        }
    }

    // สร้าง Flow เพื่อตรวจสอบสถานะล็อกอิน (เลียนแบบ Flow ใน UseCase เดิม)

}
