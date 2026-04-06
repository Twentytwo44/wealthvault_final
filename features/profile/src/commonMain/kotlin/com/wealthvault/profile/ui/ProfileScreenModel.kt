package com.wealthvault.profile.ui

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.profile.data.ProfileRepositoryImpl
import com.wealthvault.`user-api`.model.UserData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ProfileScreenModel(
private val repository: ProfileRepositoryImpl
) : ScreenModel {

    // 1. สร้าง State ไว้รอรับข้อมูล (เริ่มต้นเป็น null ไปก่อน)
    private val _userState = MutableStateFlow<UserData?>(null)

    init {
        fetchUser()
    }
    private fun fetchUser() {
        // 2. เรียกใช้งานฟังก์ชันที่ทำงานแบบ suspend ต้องทำใน Coroutine
        screenModelScope.launch {

            val result = repository.getUser()

            // 3. แกะกล่อง Result เพื่อเอาค่าไปใช้งานต่อ!
            result.onSuccess { userData ->
                // ✅ ได้ค่ามาแล้ว! เอาไปยัดใส่ State เพื่อให้ UI อัปเดต
                _userState.value = userData


            }.onFailure { error ->
                // 🚨 ถ้าพัง ก็จัดการโชว์ Error ตรงนี้
                println("Failed to get user: ${error.message}")
            }
        }
    }
}
