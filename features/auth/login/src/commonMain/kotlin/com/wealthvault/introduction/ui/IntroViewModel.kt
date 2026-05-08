package com.wealthvault.introduction.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.introduction.data.IntroRepositoryImpl
import com.wealthvault.`user-api`.model.UpdateUserDataRequest
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

class IntroScreenModel(
    private val repository: IntroRepositoryImpl
) : ScreenModel {

    // --- UI State (Form Data) ---
    var firstName by mutableStateOf("")
    var lastName by mutableStateOf("")
    var phoneNum by mutableStateOf("")
    var birthday by mutableStateOf("") // แนะนำ Format: YYYY-MM-DD
    var picture by mutableStateOf<ByteArray?>(null) // เก็บก้อนข้อมูลรูปภาพ

    // --- UI State (Status) ---
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    // ฟังก์ชันอัปเดตข้อมูล
    fun updateProfile(onSuccess: () -> Unit) {
        if (firstName.isBlank() || lastName.isBlank() || phoneNum.isBlank() || birthday.isBlank()) {
            errorMessage = "กรุณากรอกข้อมูลให้ครบถ้วน"
            return
        }

        screenModelScope.launch {
            isLoading = true
            errorMessage = null

            val request = UpdateUserDataRequest(
                firstName = firstName,
                lastName = lastName,
                phoneNumber = phoneNum,
                profileImage = picture,
                birthday = birthday,
                username = "",
                sharedEnabled = null,
                sharedAge = null
            )

            // 🚀 ยิง API และรอจนกว่าจะได้ Result (suspend function)
            val result = repository.updateUser(request)

            // เมื่อได้ Result มาแล้วค่อยจัดการต่อ
            result.onSuccess {
                isLoading = false
                println("✅ Update Success")
                onSuccess() // 🚩 เรียก callback เพื่อเปลี่ยนหน้า "หลังจาก" API สำเร็จแล้วเท่านั้น
            }.onFailure { e ->
                if (e is CancellationException) {
                    println("⚠️ Job was cancelled, but we might not want to show this as an error to user")
                } else {
                    isLoading = false
                    errorMessage = e.message ?: "เกิดข้อผิดพลาดในการอัปเดตข้อมูล"
                    println("❌ Update Failure: ${e.message}")
                }
            }
        }
    }
}
