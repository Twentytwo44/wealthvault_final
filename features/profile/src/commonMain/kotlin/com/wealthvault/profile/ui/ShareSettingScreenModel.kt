package com.wealthvault.profile.ui

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.profile.data.ProfileRepositoryImpl
import com.wealthvault.`user-api`.model.CloseFriendData
import com.wealthvault.`user-api`.model.FriendData
import com.wealthvault.`user-api`.model.UpdateUserDataRequest
import com.wealthvault.`user-api`.model.UserData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ShareSettingScreenModel(
    private val repository: ProfileRepositoryImpl
) : ScreenModel {

    private val _userState = MutableStateFlow<UserData?>(null)
    val userState = _userState.asStateFlow()

    private val _closeFriends = MutableStateFlow<List<CloseFriendData>>(emptyList())
    val closeFriends = _closeFriends.asStateFlow()

    private val _allFriends = MutableStateFlow<List<FriendData>>(emptyList())
    val allFriends = _allFriends.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving = _isSaving.asStateFlow()

    // --- 1. ดึงข้อมูล User ---
    fun fetchUser() {
        screenModelScope.launch {
            repository.getUser()
                .onSuccess { user -> _userState.value = user }
                .onFailure { error -> println("🚨 Fetch User Failed: ${error.message}") }
        }
    }

    // --- 2. ดึงข้อมูลคนใกล้ชิด (Close Friends) ---
    fun fetchCloseFriends() {
        screenModelScope.launch {
            repository.getCloseFriends()
                .onSuccess { friends -> _closeFriends.value = friends }
                .onFailure { error -> println("🚨 Fetch Close Friends Failed: ${error.message}") }
        }
    }

    // --- 3. ดึงข้อมูลเพื่อนทั้งหมด (เพื่อเลือกเพิ่มเป็นคนใกล้ชิด) ---
    fun fetchAllFriends() {
        screenModelScope.launch {
            repository.getAllFriends()
                .onSuccess { friends -> _allFriends.value = friends }
                .onFailure { error -> println("🚨 Fetch All Friends Failed: ${error.message}") }
        }
    }

    // --- 4. อัปเดตการตั้งค่าการแชร์ (Auto-save) ---
    fun updateShareSettings(sharedEnabled: Boolean, sharedAge: Int) {
        val currentUser = _userState.value ?: return
        _isSaving.value = true

        val formattedBirthday = currentUser.birthday.take(10)

        val request = UpdateUserDataRequest(
            username = currentUser.username,
            firstName = currentUser.firstName,
            lastName = currentUser.lastName,
            birthday = formattedBirthday,
            phoneNumber = currentUser.phoneNumber,
            sharedEnabled = sharedEnabled,
            sharedAge = sharedAge
        )

        screenModelScope.launch {
            repository.updateUserData(request).onSuccess {
                println("✅ อัปเดตการตั้งค่าการแชร์สำเร็จ! Enabled=$sharedEnabled, Age=$sharedAge")
                // อัปเดต State ในเครื่องด้วยค่าที่ส่งไปล่าสุด
                _userState.value = currentUser.copy(
                    sharedEnabled = sharedEnabled,
                    sharedAge = sharedAge
                )
            }.onFailure { error ->
                println("🚨 อัปเดตล้มเหลว: ${error.message}")
            }
            _isSaving.value = false
        }
    }

    // --- 5. ลบคนใกล้ชิด (ตั้งค่า is_close = false) ---
    fun removeCloseFriend(friendId: String) {
        val currentFriends = _closeFriends.value
        // Optimistic UI update
        _closeFriends.value = currentFriends.filter { it.id != friendId }

        screenModelScope.launch {
            repository.setCloseFriend(friendId = friendId, isClose = false)
                .onSuccess {
                    println("✅ ลบคนสนิทสำเร็จ! ID: $friendId")
                }
                .onFailure { error ->
                    println("🚨 ลบล้มเหลว: ${error.message}")
                    _closeFriends.value = currentFriends // Rollback
                }
        }
    }

    // --- 6. เพิ่มคนใกล้ชิด (ตั้งค่า is_close = true) ---
    fun addCloseFriends(friendIds: List<String>) {
        screenModelScope.launch {
            friendIds.forEach { id ->
                repository.setCloseFriend(id, true)
            }
            // เมื่อเพิ่มเสร็จ ดึงข้อมูลใหม่มาโชว์
            fetchCloseFriends()
        }
    }
}