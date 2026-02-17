package com.example.register.data

import com.wealthvault.`auth-api`.model.RegisterRequest

class RegisterRepositoryImpl(
    private val networkDataSource: RegisterDataSource,
) {
    suspend fun register(request: RegisterRequest): Result<Unit> {
        return networkDataSource.register(request).map { userId ->
           println("userId: $userId")
        }
    }

    // สร้าง Flow เพื่อตรวจสอบสถานะล็อกอิน (เลียนแบบ Flow ใน UseCase เดิม)

}
