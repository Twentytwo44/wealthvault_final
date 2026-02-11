package com.wealthvault.login.usecase

import com.wealthvault.login.data.LoginResponse

//class LoginUseCase(private val repository: AuthRepository) {
//
//    // ใช้ operator invoke เพื่อให้เรียกใช้ได้เหมือนฟังก์ชัน: loginUseCase(...)
//    suspend operator fun invoke(username: String, pass: String): Result<LoginResponse> {
//
//        // 💡 ใส่ Business Logic ตรงนี้ได้ (ตัวอย่าง)
//        if (username.isBlank() || pass.isBlank()) {
//            return Result.failure(Exception("Username or Password cannot be empty"))
//        }
//
//        // ส่งต่อให้ Repository จัดการ
//        return repository.login(username, pass)
//    }
//}
