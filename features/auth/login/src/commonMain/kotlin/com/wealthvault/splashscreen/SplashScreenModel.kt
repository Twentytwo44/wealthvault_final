package com.wealthvault.splashscreen

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.data_store.TokenStore
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
                mutableState.value = SplashState.GoToMain
            }
        }
    }
}
