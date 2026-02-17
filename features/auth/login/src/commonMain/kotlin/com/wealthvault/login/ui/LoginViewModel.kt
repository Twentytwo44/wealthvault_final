package com.example.login.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.`auth-api`.model.LoginRequest
import com.wealthvault.login.data.AuthRepositoryImpl
import kotlinx.coroutines.launch

class LoginScreenModel(
    private val repository: AuthRepositoryImpl
) : ScreenModel {

    // State สำหรับจัดการ UI
    var username by mutableStateOf("")
    var password by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)


    fun onLoginClick(onSuccess: () -> Unit) {
        if (username.isBlank() || password.isBlank()) {
            errorMessage = "กรุณากรอกข้อมูลให้ครบถ้วน"
            return
        }

        screenModelScope.launch {
            isLoading = true
            errorMessage = null

            val request = LoginRequest(username, password)
            val result = repository.login(request)

            isLoading = false

            result.onSuccess {
                onSuccess() // เปลี่ยนหน้าเมื่อสำเร็จ
            }.onFailure {
                errorMessage = it.message ?: "เกิดข้อผิดพลาดในการเข้าสู่ระบบ"
            }
        }
    }
}
