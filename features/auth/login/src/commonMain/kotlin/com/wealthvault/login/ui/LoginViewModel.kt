package com.example.login.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.google_auth.GoogleAuthRepository
import com.wealthvault.`auth-api`.model.LoginRequest
import com.wealthvault.core.FlowResult
import com.wealthvault.login.usecase.LoginUseCase
import kotlinx.coroutines.launch

class LoginScreenModel(
    private val loginUseCase: LoginUseCase,
    private val googelRepository: GoogleAuthRepository
) : ScreenModel {

    // UI State
    var username by mutableStateOf("")
    var password by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)


    fun onLoginClick(onSuccess: () -> Unit) {
        println("🚀 [LoginScreenModel] onLoginClick triggered")

        if (username.isBlank() || password.isBlank()) {
            errorMessage = "กรุณากรอกข้อมูลให้ครบถ้วน"
            return
        }

        screenModelScope.launch {
            isLoading = true
            errorMessage = null

            val request = LoginRequest(username, password)

            // ✅ แก้ไข: เรียกใช้ loginUseCase โดยส่ง request เข้าไปตรงๆ
            // การเรียก loginUseCase(request) จะไปเรียก invoke operator ที่ส่งต่อไปยัง execute ให้อัตโนมัติ
            loginUseCase(request).collect { flowResult ->
                when (flowResult) {
                    // 1. จัดการเมื่อเริ่มทำงาน (Loading)
                    is FlowResult.Start -> {
                        println("⏳ [LoginScreenModel] UseCase Started...")
                        isLoading = true
                        errorMessage = null
                    }

                    // 2. จัดการเมื่อทำงานสำเร็จ
                    is FlowResult.Continue -> {
                        if (flowResult.data) {
                            println("🎉 [LoginScreenModel] Login Success!")
                            isLoading = false
                            onSuccess()
                        }
                    }

                    // 3. จัดการเมื่อเกิด Error
                    is FlowResult.Failure -> {
                        println("❌ [LoginScreenModel] UseCase Error: ${flowResult.cause?.message}")
                        isLoading = false
                        errorMessage = flowResult.cause?.message ?: "การเข้าสู่ระบบล้มเหลว"
                    }

                    // 4. จัดการเมื่อจบการทำงาน (ไม่ว่าจะสำเร็จหรือพัง)
                    is FlowResult.Ended -> {
                        println("🏁 [LoginScreenModel] UseCase Finished.")
                        // ปกติเรามักจะเช็ค isLoading = false ที่นี่เพื่อความชัวร์
                        isLoading = false
                    }
                }
            }
        }
    }

    fun onGoogleClick(onSuccess: () -> Unit) {
        screenModelScope.launch {
            isLoading = true
            errorMessage = null

            val user = googelRepository.login()

            if (user != null) {
                onSuccess()
            } else {
                errorMessage = "Google login failed"
            }

            isLoading = false
        }
    }
}
