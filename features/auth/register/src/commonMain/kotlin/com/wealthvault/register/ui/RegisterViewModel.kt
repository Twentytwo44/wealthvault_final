package com.wealthvault.register.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.`auth-api`.model.RegisterRequest // 🌟 นำเข้า Model
import com.wealthvault.core.FlowResult // 🌟 นำเข้า FlowResult
import com.wealthvault.register.usecase.RegisterUseCase // 🌟 นำเข้า UseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RegisterScreenModel(
    private val registerUseCase: RegisterUseCase // 🌟 1. Inject UseCase เข้ามา
) : ScreenModel {

    var username by mutableStateOf("")
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun onRegisterClick(onSuccess: () -> Unit) {
        if (isLoading) return

        // ดักเคส: กรอกข้อมูลไม่ครบ
        if (username.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            errorMessage = "กรุณากรอกข้อมูลให้ครบถ้วน"
            return
        }

        // ดักเคส: รหัสผ่าน 2 ช่องไม่ตรงกัน
        if (password != confirmPassword) {
            errorMessage = "รหัสผ่านไม่ตรงกัน"
            return
        }

        // 🌟 2. เรียกใช้ UseCase จริงแทนการ delay()
        screenModelScope.launch {
            // 💡 สร้าง Request (เช็คชื่อตัวแปร email/username ให้ตรงกับที่ RegisterRequest ต้องการนะครับ)
            val request = RegisterRequest(
                email = username,
                password = password
            )

            // ยิง API ผ่าน UseCase
            registerUseCase(request).collect { flowResult ->
                when (flowResult) {
                    // ⏳ กำลังโหลด
                    is FlowResult.Start -> {
                        println("⏳ [RegisterScreenModel] กำลังส่งข้อมูลสมัครสมาชิก...")
                        isLoading = true
                        errorMessage = null
                    }

                    // 🎉 สำเร็จ
                    is FlowResult.Continue -> {
                        if (flowResult.data) {
                            println("🎉 [RegisterScreenModel] สมัครสมาชิกสำเร็จ! ย้ายไปหน้า Login")
                            isLoading = false
                            onSuccess()
                        }
                    }

                    // ❌ พัง / Error
                    is FlowResult.Failure -> {
                        println("❌ [RegisterScreenModel] สมัครล้มเหลว: ${flowResult.cause?.message}")
                        isLoading = false
                        errorMessage = flowResult.cause?.message ?: "การสมัครสมาชิกล้มเหลว กรุณาลองใหม่"
                    }

                    // 🏁 จบการทำงาน
                    is FlowResult.Ended -> {
                        println("🏁 [RegisterScreenModel] สิ้นสุดกระบวนการสมัครสมาชิก")
                        isLoading = false
                    }
                }
            }
        }
    }

    // ฟังก์ชันจำลองเมื่อกดสมัครด้วย Google (เผื่อทำต่อ)
    fun onGoogleClick(onSuccess: () -> Unit) {
        if (isLoading) return

        screenModelScope.launch {
            isLoading = true
            errorMessage = null
            delay(1500)
            isLoading = false
            onSuccess()
        }
    }
}