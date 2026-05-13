package com.wealthvault.splashscreen

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.data_store.TokenStore
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
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

    // 🌟 ย้ายจาก init มาสร้างฟังก์ชันให้เรียกจากภายนอกได้
    fun checkAuthentication() {
        screenModelScope.launch {
            try {
                // รวมจังหวะหน่วงเวลาให้พอดี (ประมาณ 1.5 - 2 วินาที)
                delay(1700)

                // 🌟 ใช้ firstOrNull เพื่อความปลอดภัย ไม่ให้ Flow ค้าง
                val token = tokenStore.accessToken.firstOrNull()

                println("check token: $token")

                if (token.isNullOrBlank()) {
                    mutableState.value = SplashState.GoToLogin
                } else {
                    // 💡 ตรงนี้ในอนาคตถ้าจะเช็กโปรไฟล์/วันเกิด ให้ยิง API ต่อที่นี่ได้เลย
                    mutableState.value = SplashState.GoToMain
                }
            } catch (e: Exception) {
                println("❌ Splash Error: ${e.message}")
                // ถ้า Error ให้ส่งไป Login เพื่อความปลอดภัย
                mutableState.value = SplashState.GoToLogin
            }
        }
    }
}
