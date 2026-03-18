package com.wealthvault.register.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RegisterScreenModel : ScreenModel {

    // 🌟 1. ตัวแปรเก็บค่าข้อมูล (State) ที่ผูกกับหน้าจอ
    var username by mutableStateOf("")
    var password by mutableStateOf("")
    var isLoading by mutableStateOf(false)

    // 🌟 2. ฟังก์ชันจัดการเมื่อกดปุ่ม "สร้างบัญชี"
    fun onRegisterClick(onSuccess: () -> Unit) {
        // ดักไว้ก่อน: ถ้าระบบกำลังโหลดอยู่ (หมุนๆ) ห้ามให้กดซ้ำ
        if (isLoading) return

        // ดักไว้ก่อน: ถ้ากรอกข้อมูลไม่ครบ ห้ามไปต่อ
        if (username.isBlank() || password.isBlank()) {
            // TODO: อนาคตอาจจะเพิ่มการโชว์ Error สีแดงๆ ใต้ช่องกรอก
            println("กรุณากรอกข้อมูลให้ครบถ้วน")
            return
        }

        // เริ่มทำงานเบื้องหลัง (Background Task)
        screenModelScope.launch {
            isLoading = true

            // ⏳ [จำลอง] การส่งข้อมูลไปหา Server / Firebase (หน่วงเวลา 2 วินาที)
            // TODO: เดี๋ยวเราค่อยมาต่อ API ของจริงตรงนี้ครับ
            delay(2000)

            isLoading = false

            // ✅ ทำงานสำเร็จ! ส่งสัญญาณกลับไปบอก UI ให้เปลี่ยนหน้าได้เลย
            onSuccess()
        }
    }

    // 🌟 3. ฟังก์ชันจำลองเมื่อกดสมัครด้วย Google
    fun onGoogleClick(onSuccess: () -> Unit) {
        if (isLoading) return

        screenModelScope.launch {
            isLoading = true
            delay(1500) // จำลองการโหลด
            isLoading = false
            onSuccess()
        }
    }
}