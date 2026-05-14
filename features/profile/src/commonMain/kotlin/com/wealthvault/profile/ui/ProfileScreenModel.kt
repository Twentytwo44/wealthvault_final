package com.wealthvault.profile.ui

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.profile.data.ProfileRepositoryImpl
import com.wealthvault.`user-api`.model.CloseFriendData
import com.wealthvault.`user-api`.model.UserData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileScreenModel(
    private val repository: ProfileRepositoryImpl
) : ScreenModel {

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _userState = MutableStateFlow<UserData?>(null)
    val userState = _userState.asStateFlow()

    private val _closeFriends = MutableStateFlow<List<CloseFriendData>>(emptyList())
    val closeFriends = _closeFriends.asStateFlow()

    init {
        // 🌟 เรียกฟังก์ชันเดียวพอครับ
        fetchProfileData()
    }

    // 🌟 ยุบรวมการดึงข้อมูลมาไว้ในฟังก์ชันเดียว แบบต่อแถวกันทำงาน (Sequential)
    fun fetchProfileData() {
        screenModelScope.launch {
            _isLoading.value = true

            // 1. ยิง API ดึงข้อมูลผู้ใช้ก่อน
            repository.getUser()
                .onSuccess { userData ->
                    _userState.value = userData
                }
                .onFailure { error ->
                    println("🚨 Failed to get user: ${error.message}")
                }

            // 2. รอจนกว่า User จะดึงเสร็จ (หรือพัง) ค่อยยิง API ดึงเพื่อนต่อ!
            // ป้องกันเซิร์ฟเวอร์เตะเพราะโดนรุมยิงพร้อมกัน
            repository.getCloseFriends()
                .onSuccess { friends ->
                    _closeFriends.value = friends
                }
                .onFailure { error ->
                    println("❌ [API ERROR] Get Close Friends Failed: ${error.message}")
                }

            _isLoading.value = false
        }
    }
}