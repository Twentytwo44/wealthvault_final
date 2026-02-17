package com.wealthvault.fogetpassword.data

import com.wealthvault.`auth-api`.model.ResetPasswordRequest

class ResetRepositoryImpl(
    private val networkDataSource: ResetNetworkDataSource,
) {
    suspend fun reset(request: ResetPasswordRequest): Result<Unit> {
        return networkDataSource.forgetPassword(request).map { token ->
            println("[ResetRepositoryImpl] Reset Password Succes")
        }
    }

    // สร้าง Flow เพื่อตรวจสอบสถานะล็อกอิน (เลียนแบบ Flow ใน UseCase เดิม)

}
