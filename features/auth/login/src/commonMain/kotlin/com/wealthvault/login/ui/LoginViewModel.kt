package com.wealthvault.login.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.`auth-api`.model.LoginRequest
import com.wealthvault.core.FlowResult
import com.wealthvault.data_store.DeviceInfo
import com.wealthvault.data_store.TokenStore
import com.wealthvault.google_auth.GoogleAuthRepository
import com.wealthvault.login.data.device.RegisterDeviceRepositoryImpl
import com.wealthvault.login.usecase.LoginUseCase
import com.wealthvault.notification_api.model.DeviceRequest
import com.wealthvault_final.line_auth.LineAuth
import com.wealthvault_final.line_auth.model.LineUser
import com.wealthvault_final.notification.PushNotificationHelper
import kotlinx.coroutines.launch

class LoginScreenModel(
    private val loginUseCase: LoginUseCase,
    private val googelRepository: GoogleAuthRepository,
    private val pushHelper: PushNotificationHelper,
    private val addDeviceRepository: RegisterDeviceRepositoryImpl,
    private val tokenStore: TokenStore
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

            loginUseCase(request).collect { flowResult ->
                when (flowResult) {
                    is FlowResult.Start -> {
                        println("⏳ [LoginScreenModel] UseCase Started...")
                        isLoading = true
                        errorMessage = null
                    }

                    is FlowResult.Continue -> {
                        if (flowResult.data) {
                            println("🎉 [LoginScreenModel] Login Success!")
                            isLoading = false
                            onSuccess()
                        }
                    }

                    // 🌟 จัดการแจ้งเตือน Error ให้ครอบคลุม
                    is FlowResult.Failure -> {
                        println("❌ [LoginScreenModel] UseCase Error: ${flowResult.cause?.message}")
                        isLoading = false

                        // ดึงข้อความ Error ดิบๆ มา (แปลงเป็นตัวเล็กให้หมดเพื่อเช็คง่ายๆ)
                        val rawError = flowResult.cause?.message?.lowercase() ?: ""

                        errorMessage = when {
                            // 1. รหัสผ่านหรืออีเมลผิด (อ้างอิงจาก JSON Error ของ Backend)
                            rawError.contains("invalid email or password") -> {
                                "อีเมลหรือรหัสผ่านไม่ถูกต้อง"
                            }

                            // 2. ไม่พบผู้ใช้งาน
                            rawError.contains("user not found") || rawError.contains("not exist") -> {
                                "ไม่พบบัญชีผู้ใช้งานนี้ในระบบ"
                            }

                            // 3. ปัญหาการเชื่อมต่อเครือข่าย หรือ Server ดาวน์
                            rawError.contains("network") ||
                                    rawError.contains("timeout") ||
                                    rawError.contains("refused") ||
                                    rawError.contains("failed to connect") -> {
                                "ไม่สามารถเชื่อมต่อเซิร์ฟเวอร์ได้ กรุณาลองใหม่อีกครั้ง"
                            }

                            // 4. กรณีกรอกข้อมูลมาในรูปแบบไม่ถูกต้อง (เช่น อีเมลไม่มี @)
                            rawError.contains("bad request") || rawError.contains("invalid request") -> {
                                "รูปแบบข้อมูลไม่ถูกต้อง กรุณาตรวจสอบอีกครั้ง"
                            }

                            // 5. กรณี Server มีปัญหาภายใน (Internal Error แต่ไม่ใช่เรื่องรหัสผิด)
                            rawError.contains("internal") || rawError.contains("500") -> {
                                "ระบบขัดข้องชั่วคราว กรุณาลองใหม่ในภายหลัง"
                            }

                            // 6. ถ้ามี Error อะไรที่หลุดรอดมาได้ และมีข้อความ (กันไว้ก่อน)
                            rawError.isNotBlank() -> {
                                // พิมพ์ลง Log เพื่อให้นักพัฒนาดู
                                println("⚠️ Unhandled Error: $rawError")
                                // แต่โชว์ให้ User เห็นแบบซอฟต์ๆ
                                "การเข้าสู่ระบบล้มเหลว กรุณาลองใหม่อีกครั้ง"
                            }

                            // 7. ไม่มี Error Message โผล่มาเลย
                            else -> {
                                "เกิดข้อผิดพลาดที่ไม่ทราบสาเหตุ กรุณาลองใหม่อีกครั้ง"
                            }
                        }
                    }

                    is FlowResult.Ended -> {
                        println("🏁 [LoginScreenModel] UseCase Finished.")
                        isLoading = false
                    }
                }
            }
        }
    }

    fun onGetFCMToken(){
        pushHelper.getDeviceTokenInfo(
            onSuccess = { deviceInfo ->

                println("============================================================")
                println("Test FCM Token: ${deviceInfo.fcmToken}")
                println("Platform: ${deviceInfo.platform}")
                println("Device Name: ${deviceInfo.deviceName}")
                println("============================================================")
                // 3. 🟢 นำข้อมูลที่ดึงได้ ประกอบร่างยิง API ไปเก็บที่ Django
                screenModelScope.launch {
                    try {
                        val request = DeviceRequest(
                            token = deviceInfo.fcmToken,
                            platform = deviceInfo.platform,
                            deviceName = deviceInfo.deviceName
                        )
                        val info = DeviceInfo(
                            fcmToken = deviceInfo.fcmToken,
                            platform = deviceInfo.platform,
                            deviceName = deviceInfo.deviceName,

                            )
                        tokenStore.saveDeviceInfo(info)
                        addDeviceRepository.addDevice(request)
                        println("✅ ส่ง Device Token ขึ้น Server สำเร็จ!")
                    } catch (e: Exception) {
                        println("❌ ส่ง Device Token ไม่สำเร็จ: ${e.message}")
                    }
                }
            },
            onError = { error ->
                println("❌ ไม่สามารถดึง FCM Token จากเครื่องได้: $error")
            }
        )
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



//        lineAuth.login()
    }

    fun onLineSuccess(user: LineUser, onSuccess: () -> Unit) {
        println("🎉 [LoginScreenModel] LINE Success: ${user.displayName} (${user.userId})")
        isLoading = false
        onSuccess()
    }

    fun onLineError(error: String) {
        println("❌ [LoginScreenModel] LINE Error: $error")
        isLoading = false
        errorMessage = error
    }
}
