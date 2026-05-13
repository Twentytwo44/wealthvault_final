package com.wealthvault.login.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.`auth-api`.model.LoginRequest
import com.wealthvault.`auth-api`.model.TokenRequest
import com.wealthvault.core.FlowResult
import com.wealthvault.data_store.DeviceInfo
import com.wealthvault.data_store.TokenStore
import com.wealthvault.google_auth.GoogleAuthRepository
import com.wealthvault.login.data.device.RegisterDeviceRepositoryImpl
import com.wealthvault.login.data.google.GoogleRepositoryImpl
import com.wealthvault.login.usecase.LoginUseCase
import com.wealthvault.notification_api.model.DeviceRequest
import com.wealthvault.splashscreen.data.UserRepositoryImpl
import com.wealthvault_final.notification.PushNotificationHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

sealed class LoginState {
    object Loading : LoginState()
    object GoToIntro : LoginState()
    object GoToMain : LoginState()
}

class LoginScreenModel(
    private val loginUseCase: LoginUseCase,
    private val googleRepository: GoogleAuthRepository,
    private val pushHelper: PushNotificationHelper,
    private val addDeviceRepository: RegisterDeviceRepositoryImpl,
    private val tokenStore: TokenStore,
    private val authRepository: UserRepositoryImpl,
    private val googleLink: GoogleRepositoryImpl
) : ScreenModel {

    // UI State
    var username by mutableStateOf("")
    var password by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    // 🌟 ฟังก์ชันสำหรับแปลง Error จาก Backend / Network ให้เป็นภาษาไทยที่อ่านง่าย
    private fun parseErrorMessage(rawError: String?): String {
        if (rawError.isNullOrBlank()) return "เกิดข้อผิดพลาดบางอย่าง กรุณาลองใหม่"

        val lowerError = rawError.lowercase()

        return when {
            // เคสพิมพ์ผิด (อิงจาก RPC Error ของหลังบ้าน)
            lowerError.contains("invalid email or password") ||
                    lowerError.contains("wrong password") ||
                    lowerError.contains("user not found") -> "อีเมลหรือรหัสผ่านไม่ถูกต้อง"

            // เคสปัญหาเครือข่าย/เน็ตหลุด/เซิร์ฟเวอร์ตาย
            lowerError.contains("timeout") ||
                    lowerError.contains("failed to connect") ||
                    lowerError.contains("unknownhost") ||
                    lowerError.contains("network") ||
                    lowerError.contains("connection refused") -> "ไม่สามารถเชื่อมต่อเซิร์ฟเวอร์ได้"

            // เคสอื่นๆ ที่ไม่ได้ดักไว้ (ตัดคำว่า rpc error ทิ้งถ้ามี เพื่อให้ดูสะอาดขึ้น)
            else -> "เข้าสู่ระบบไม่สำเร็จ: ${rawError.replace("rpc error: code = Internal desc = ", "")}"
        }
    }

    fun onLoginClick(onNavigate: (LoginState) -> Unit) {
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
                    is FlowResult.Start -> { isLoading = true }

                    is FlowResult.Continue -> {
                        if (flowResult.data) {
                            println("🎉 Login Success! กำลังเช็คข้อมูลส่วนตัว...")
                            checkUserDataAndNavigate(onNavigate)
                        }
                    }

                    is FlowResult.Failure -> {
                        isLoading = false
                        // 🌟 นำข้อความ Error ไปผ่านตัวกรองก่อนแสดงผล
                        errorMessage = parseErrorMessage(flowResult.cause?.message)
                    }

                    is FlowResult.Ended -> {
                        // ปล่อยผ่าน
                    }
                }
            }
        }
    }

    private suspend fun checkUserDataAndNavigate(onNavigate: (LoginState) -> Unit) {
        try {
            delay(200)
            onGetFCMToken()
            delay(200)

            val userResult = authRepository.getUser()

            if (userResult.isFailure) {
                throw Exception(userResult.exceptionOrNull()?.message ?: "ดึงข้อมูลโปรไฟล์ล้มเหลว")
            }

            val userData = userResult.getOrNull()
            val birthday = userData?.birthday

            if (birthday.isNullOrBlank() || birthday.startsWith("1970-01-01")) {
                println("🐣 No Birthday found -> Go To Intro")
                onNavigate(LoginState.GoToIntro)
            } else {
                println("✅ Birthday exists -> Go To Main")
                onNavigate(LoginState.GoToMain)
            }
        } catch (e: Exception) {
            println("❌ Error checking user data: ${e.message}")
            // 🌟 ผ่านตัวกรองเผื่อว่าเน็ตหลุดตอนเช็คข้อมูล User พอดี
            errorMessage = "ดึงข้อมูลโปรไฟล์ไม่สำเร็จ: " + parseErrorMessage(e.message)
        } finally {
            isLoading = false
        }
    }

    fun onGetFCMToken() {
        pushHelper.getDeviceTokenInfo(
            onSuccess = { deviceInfo ->
                println("============================================================")
                println("Test FCM Token: ${deviceInfo.fcmToken}")
                println("Platform: ${deviceInfo.platform}")
                println("Device Name: ${deviceInfo.deviceName}")
                println("============================================================")

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

    fun onGoogleClick(onNavigate: (LoginState) -> Unit) {
        screenModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                val user = googleRepository.login()

                println("Google User = $user")

                if (user == null) {
                    errorMessage = "ยกเลิกการเข้าสู่ระบบผ่าน Google"
                    isLoading = false
                    return@launch
                }

                val request = TokenRequest(
                    token = user.idToken
                )

                val response = googleLink.glogin(request)

                response.onSuccess { data ->
                    if (data.success == true) {
                        println("🎉 Google Login Success!")
                        checkUserDataAndNavigate(onNavigate)
                    } else {
                        errorMessage = "เข้าสู่ระบบด้วย Google ไม่สำเร็จ"
                        isLoading = false
                    }
                }

                response.onFailure { exception ->
                    exception.printStackTrace()
                    // 🌟 นำข้อความ Error ไปผ่านตัวกรองก่อนแสดงผล
                    errorMessage = parseErrorMessage(exception.message)
                    isLoading = false
                }

            } catch (e: Exception) {
                e.printStackTrace()
                // 🌟 นำข้อความ Error ไปผ่านตัวกรองก่อนแสดงผล
                errorMessage = parseErrorMessage(e.message)
                isLoading = false
            }
        }
    }
}