package com.wealthvault.register.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.`auth-api`.model.RegisterRequest
import com.wealthvault.core.FlowResult
import com.wealthvault.register.usecase.RegisterUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RegisterScreenModel(
    private val registerUseCase: RegisterUseCase
) : ScreenModel {

    var username by mutableStateOf("")
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun onRegisterClick(onSuccess: () -> Unit) {
        if (isLoading) return

        // 🌟 1. ดักเคส: กรอกข้อมูลไม่ครบ
        if (username.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            errorMessage = "กรุณากรอกข้อมูลให้ครบถ้วน"
            return
        }

        // 🌟 2. ดักเคส: รหัสผ่าน 2 ช่องไม่ตรงกัน
        if (password != confirmPassword) {
            errorMessage = "รหัสผ่านไม่ตรงกัน"
            return
        }

        // เริ่มโหลดและล้าง Error เก่าทิ้ง
        isLoading = true
        errorMessage = null

        screenModelScope.launch {
            val request = RegisterRequest(
                email = username,
                password = password
            )

            // 🌟 3. ยิง API ผ่าน UseCase
            registerUseCase(request).collect { flowResult ->
                when (flowResult) {
                    is FlowResult.Start -> {
                        println("⏳ [RegisterScreenModel] กำลังส่งข้อมูลสมัครสมาชิก...")
                        isLoading = true
                    }
                    is FlowResult.Continue -> {
                        if (flowResult.data) {
                            println("🎉 [RegisterScreenModel] สมัครสมาชิกสำเร็จ! ย้ายไปหน้า Login")
                            isLoading = false
                            onSuccess()
                        }
                    }
                    is FlowResult.Failure -> {
                        println("❌ [RegisterScreenModel] สมัครล้มเหลว: ${flowResult.cause?.message}")
                        isLoading = false
                        // 🌟 4. ดึง Error จาก Backend มาโชว์บนหน้าจอ
                        errorMessage = flowResult.cause?.message ?: "การสมัครสมาชิกล้มเหลว กรุณาลองใหม่"
                    }
                    is FlowResult.Ended -> {
                        println("🏁 [RegisterScreenModel] สิ้นสุดกระบวนการสมัครสมาชิก")
                        // ไม่ต้องทำอะไรตรงนี้ เพราะ isLoading ถูกจัดการใน Continue/Failure ไปแล้ว
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