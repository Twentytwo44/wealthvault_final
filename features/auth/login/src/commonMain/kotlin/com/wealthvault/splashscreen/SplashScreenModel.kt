package com.wealthvault.splashscreen

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.splashscreen.data.UserRepositoryImpl
import com.wealthvault.`user-api`.model.UserData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SplashScreenModel(
    private val userRepository: UserRepositoryImpl
): ScreenModel {

    private val _state = MutableStateFlow<UserData>(UserData())
    val state = _state.asStateFlow()

    init {
        fetchUser()
    }


    private fun fetchUser() {
        // ใช้ screenModelScope เพื่อยิง API (ทำงานแบบ Background Thread)

        screenModelScope.launch {

            try {
                val result = userRepository.getUser()
                val userResponse = result.getOrNull()
                if (result.isSuccess && userResponse != null) {
                    _state.value = userResponse
                }
                else {
                    println("❌ [ScreenModel] Create Asset Failed")
                }



            } catch (e: Exception) {
                // ถ้าเน็ตหลุด หรือ API พัง ให้เปลี่ยนสถานะเป็น Error

            }
        }
    }

}


