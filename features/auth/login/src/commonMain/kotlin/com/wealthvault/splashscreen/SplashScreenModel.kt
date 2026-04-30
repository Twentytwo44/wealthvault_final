package com.wealthvault.splashscreen

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.data_store.TokenStore
import com.wealthvault.splashscreen.data.UserRepositoryImpl
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

sealed class SplashState {
    object Loading : SplashState()
    object GoToLogin : SplashState()
    object GoToIntro : SplashState()
    object GoToMain : SplashState()
}

class SplashScreenModel(
    private val tokenStore: TokenStore,
    private val authRepository: UserRepositoryImpl
) : StateScreenModel<SplashState>(SplashState.Loading) {

    init { checkAuthentication() }

    private fun checkAuthentication() {
        screenModelScope.launch {
            // 1. ดึง Token จากเครื่อง
            val token = tokenStore.accessToken.first()

            if (token.isNullOrBlank()) {
                // ไม่มี Token = ไปหน้า Login
                mutableState.value = SplashState.GoToLogin
            } else {
                // มี Token = ลองดึงโปรไฟล์เพื่อเช็กวันเกิด
                try {
                    val user = authRepository.getUser()
                    if (user?.map { it.birthday } == null) {
                        mutableState.value = SplashState.GoToIntro
                    } else {
                        mutableState.value = SplashState.GoToMain
                    }
                } catch (e: Exception) {
                    // ถ้าดึงโปรไฟล์พัง (เช่น 401 และ refresh ไม่ผ่าน)
                    // ตัว Interceptor จะสั่ง tokenStore.clear() ไปแล้ว
                    // เราก็แค่ส่งไปหน้า Login
                    mutableState.value = SplashState.GoToLogin
                }
            }
        }
    }
}
