package com.wealthvault.profile.ui

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.profile.data.ProfileRepositoryImpl
import com.wealthvault.`user-api`.model.CloseFriendData
import com.wealthvault.`user-api`.model.UserData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ProfileScreenModel(
    private val repository: ProfileRepositoryImpl
) : ScreenModel {

    private val _userState = MutableStateFlow<UserData?>(null)

    private val _closeFriends = MutableStateFlow<List<CloseFriendData>>(emptyList())
    val closeFriends = _closeFriends.asStateFlow()

    // 🌟 เพิ่ม State สำหรับเก็บเพื่อนทั้งหมด (เอาไว้ใช้ใน Popup เลือกเพื่อน)
    private val _allFriends = MutableStateFlow<List<CloseFriendData>>(emptyList())
    val allFriends = _allFriends.asStateFlow()

    init {
        // โหลดข้อมูลพื้นฐานทันทีที่สร้าง Model
        fetchUser()
        fetchCloseFriends()
    }

    fun fetchUser() {
        screenModelScope.launch {
            repository.getUser()
                .onSuccess { userData ->
                    _userState.value = userData
                }
                .onFailure { error ->
                    println("🚨 Failed to get user: ${error.message}")
                }
        }
    }

    fun fetchCloseFriends() {
        screenModelScope.launch {
            repository.getCloseFriends()
                .onSuccess { friends ->
                    println("✅ [API SUCCESS] Close Friends: ${friends.size} คน")
                    _closeFriends.value = friends
                }
                .onFailure { error ->
                    println("❌ [API ERROR] Get Close Friends Failed: ${error.message}")
                }
        }
    }

    // 🌟 เพิ่มฟังก์ชันสำหรับดึง "เพื่อนทั้งหมด" (เพื่อนปกติที่ยังไม่ได้เป็น Close Friend)
    // หมายเหตุ: ตรงนี้ต้องดูว่า Repository มีฟังก์ชันดึงเพื่อนทุกคนหรือยัง
    // ถ้ายังไม่มี ให้ใช้ api.getFriendList() (หรือเส้นที่ดึงเพื่อนทั้งหมด) มาใส่ใน Repository ก่อนนะครับ
    fun fetchAllFriends() {
        screenModelScope.launch {
            // สมมติว่า repository มีฟังก์ชันดึงเพื่อนทุกคน
            // repository.getAllFriends().onSuccess { friends -> _allFriends.value = friends }
        }
    }
}