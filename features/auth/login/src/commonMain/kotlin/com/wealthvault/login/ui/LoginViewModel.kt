package com.wealthvault.login.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.google_auth.GoogleAuthRepository
import com.wealthvault.`auth-api`.model.LoginRequest
import com.wealthvault.core.FlowResult
import com.wealthvault.login.usecase.LoginUseCase
import com.wealthvault_final.line_auth.LineAuth
import com.wealthvault_final.line_auth.model.LineUser
import com.wealthvault_final.notification.PushNotificationHelper
import kotlinx.coroutines.launch

class LoginScreenModel(
    private val loginUseCase: LoginUseCase,
    private val googelRepository: GoogleAuthRepository,
    private val pushHelper: PushNotificationHelper
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

                        // 🌟 ดึงข้อความ Error ดิบๆ จาก Backend
                        val rawError = flowResult.cause?.message ?: ""

                        // 🌟 ดักจับข้อความ Error และแปลงเป็นภาษาไทยที่ User เข้าใจง่าย
                        errorMessage = when {
                            rawError.contains("invalid email or password", ignoreCase = true) -> {
                                "อีเมลหรือรหัสผ่านไม่ถูกต้อง"
                            }
                            rawError.contains("user not found", ignoreCase = true) -> {
                                "ไม่พบบัญชีผู้ใช้งานนี้ในระบบ"
                            }
                            rawError.contains("network", ignoreCase = true) || rawError.contains("timeout", ignoreCase = true) -> {
                                "ไม่สามารถเชื่อมต่อเซิร์ฟเวอร์ได้ กรุณาลองใหม่อีกครั้ง"
                            }
                            rawError.isNotBlank() -> {
                                // ถ้ามี Error แปลกๆ นอกเหนือจากที่ดักไว้ ให้โชว์ออกมาเผื่อไว้ดีบัก
                                "การเข้าสู่ระบบล้มเหลว: $rawError"
                            }
                            else -> {
                                // ถ้าไม่มี Message อะไรมาเลย
                                "การเข้าสู่ระบบล้มเหลว กรุณาลองใหม่อีกครั้ง"
                            }
                        }
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

    fun onLineClick(lineAuth: LineAuth) {
        println("🚀 [LoginScreenModel] onLineClick triggered")
        isLoading = true
        errorMessage = null

        // สั่งให้ตัวจัดการ LINE ที่หน้าจอส่งมา เริ่มทำงาน


        pushHelper.getDeviceTokenInfo(
            onSuccess = { deviceInfo ->

                println("============================================================")
                println("Test FCM Token: ${deviceInfo.fcmToken}")
                println("Platform: ${deviceInfo.platform}")
                println("Device Name: ${deviceInfo.deviceName}")
                println("============================================================")
                // 3. 🟢 นำข้อมูลที่ดึงได้ ประกอบร่างยิง API ไปเก็บที่ Django
//                coroutineScope.launch {
//                    try {
//                        val request = DeviceTokenRequest(
//                            fcmToken = deviceInfo.fcmToken,
//                            platform = deviceInfo.platform,
//                            deviceName = deviceInfo.deviceName
//                        )
//                        notificationApi.registerDevice(request)
//                        println("✅ ส่ง Device Token ขึ้น Server สำเร็จ!")
//                    } catch (e: Exception) {
//                        println("❌ ส่ง Device Token ไม่สำเร็จ: ${e.message}")
//                    }
//                }
            },
            onError = { error ->
                println("❌ ไม่สามารถดึง FCM Token จากเครื่องได้: $error")
            }
        )
//        lineAuth.login()
    }

    // 🟢 2. รับผลลัพธ์กลับมาเมื่อ LINE ล็อกอินสำเร็จ
    fun onLineSuccess(user: LineUser, onSuccess: () -> Unit) {
        println("🎉 [LoginScreenModel] LINE Success: ${user.displayName} (${user.userId})")
        isLoading = false

        // 💡 ตรงนี้คุณสามารถเอา user.userId ไปยิงเข้า API Backend ของ WealthVault ได้เลย
        // เช่น loginUseCase(LoginRequest(user.userId, ...))

        onSuccess() // สั่งให้หน้าจอทำคำสั่งต่อไป (เช่น เด้งไปหน้า Home)
    }

    // 🟢 3. รับผลลัพธ์กลับมาเมื่อพัง หรือกดยกเลิก
    fun onLineError(error: String) {
        println("❌ [LoginScreenModel] LINE Error: $error")
        isLoading = false
        errorMessage = error
    }
}