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
import com.wealthvault.splashscreen.data.UserRepositoryImpl
import com.wealthvault_final.notification.PushNotificationHelper
import kotlinx.coroutines.launch


sealed class LoginState {
    object Loading : LoginState()
    object GoToIntro : LoginState()
    object GoToMain : LoginState()
}
class LoginScreenModel(
    private val loginUseCase: LoginUseCase,
    private val googelRepository: GoogleAuthRepository,
    private val pushHelper: PushNotificationHelper,
    private val addDeviceRepository: RegisterDeviceRepositoryImpl,
    private val tokenStore: TokenStore,
    private val authRepository: UserRepositoryImpl
) : ScreenModel {

    // UI State
    var username by mutableStateOf("")
    var password by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

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

                            // 🌟 ขั้นตอนเพิ่มเติม: เช็คข้อมูล User หลัง Login สำเร็จ
                            checkUserDataAndNavigate(onNavigate)
                        }
                    }

                    is FlowResult.Failure -> {
                        isLoading = false
                        // ... (ลอจิกจัดการ errorMessage เดิมของคุณ) ...
                    }

                    is FlowResult.Ended -> { isLoading = false }
                }
            }
        }
    }

    // แยกฟังก์ชันเช็ค Birthday ออกมาเพื่อให้โค้ดสะอาด
    private suspend fun checkUserDataAndNavigate(onNavigate: (LoginState) -> Unit) {
        try {
            val userResult = authRepository.getUser()
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
            onNavigate(LoginState.GoToIntro)
        } finally {
            isLoading = false
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


}
