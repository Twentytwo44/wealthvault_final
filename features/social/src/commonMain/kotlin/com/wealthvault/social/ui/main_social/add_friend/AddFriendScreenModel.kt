package com.wealthvault.social.ui.main_social.add_friend

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.social.data.SocialRepositoryImpl
import com.wealthvault.`user-api`.model.AcceptFriendRequest
import com.wealthvault.`user-api`.model.FriendData
import com.wealthvault.`user-api`.model.PendingFriendData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddFriendScreenModel(
    private val repository: SocialRepositoryImpl
) : ScreenModel {

    // สถานะกำลังค้นหา (เอาไปใช้หมุนๆ ปุ่ม)
    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    // สถานะว่าได้เคยกดค้นหาไปแล้วหรือยัง (เอาไปโชว์ผลลัพธ์)
    private val _hasSearched = MutableStateFlow(false)
    val hasSearched: StateFlow<Boolean> = _hasSearched.asStateFlow()

    // ผลลัพธ์การค้นหา
    private val _searchResult = MutableStateFlow<FriendData?>(null)
    val searchResult: StateFlow<FriendData?> = _searchResult.asStateFlow()

    // สถานะการเพิ่มเพื่อนสำเร็จ (เอาไปเด้งกลับหน้าเดิม)
    private val _addFriendSuccess = MutableStateFlow(false)
    val addFriendSuccess: StateFlow<Boolean> = _addFriendSuccess.asStateFlow()

    // 🌟 ฟังก์ชันค้นหาผู้ใช้จาก Email
    fun searchUser(email: String) {
        screenModelScope.launch {
            _isSearching.value = true
            _hasSearched.value = true
            _searchResult.value = null // เคลียร์ผลลัพธ์เก่าทิ้งก่อน

            repository.searchUser(email).onSuccess { user ->
                _searchResult.value = user
            }.onFailure {
                _searchResult.value = null // ถ้าไม่เจอหรือพัง ให้เป็น null
            }

            _isSearching.value = false
        }
    }

    // 🌟 ฟังก์ชันส่งคำขอเพิ่มเพื่อน
    fun addFriend(targetId: String) {
        screenModelScope.launch {
            repository.addFriend(targetId).onSuccess {
                _addFriendSuccess.value = true
            }.onFailure { error ->
                println("🚨 เพิ่มเพื่อนไม่สำเร็จ: ${error.message}")
            }
        }
    }
    private val _pendingFriends = MutableStateFlow<List<PendingFriendData>>(emptyList())
    val pendingFriends: StateFlow<List<PendingFriendData>> = _pendingFriends.asStateFlow()

    // 🌟 ฟังก์ชันดึงคำขอเป็นเพื่อน (เอาไปเรียกตอนเปิดหน้าจอ)
    fun fetchPendingFriends() {
        screenModelScope.launch {
            repository.getPendingFriends().onSuccess { friends ->
                _pendingFriends.value = friends
            }
        }
    }

    // 🌟 ฟังก์ชันจัดการคำขอเป็นเพื่อน (รับ/ปฏิเสธ)
    private val _popupMessage = MutableStateFlow<String?>(null)
    val popupMessage: StateFlow<String?> = _popupMessage.asStateFlow()

    // 🌟 2. ฟังก์ชันสำหรับปิด Popup
    fun clearPopupMessage() {
        _popupMessage.value = null
    }

    // 🌟 3. อัปเดตฟังก์ชันจัดการคำขอ ให้แจ้งเตือนเมื่อเสร็จสิ้น
    fun respondToFriendRequest(requesterId: String, isAccept: Boolean) {
        screenModelScope.launch {
            val actionStr = if (isAccept) "ACCEPT" else "DECLINE"
            val request = AcceptFriendRequest(requesterId = requesterId, action = actionStr)

            repository.acceptFriend(request)
                .onSuccess {
                    // ลบคนนั้นออกจากหน้าจอ
                    _pendingFriends.value = _pendingFriends.value.filterNot { it.id == requesterId }
                    // 🌟 ตั้งค่าข้อความ Popup
                    _popupMessage.value = if (isAccept) "เพิ่มเป็นเพื่อนสำเร็จแล้ว!" else "ลบคำขอเป็นเพื่อนเรียบร้อย"
                }
                .onFailure { error ->
                    // 🌟 แจ้งเตือนกรณี Error
                    _popupMessage.value = "เกิดข้อผิดพลาด: ${error.message}"
                }
        }
    }

}