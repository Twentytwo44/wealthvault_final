package com.wealthvault.social.ui.main_social.form_group

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.social.data.SocialRepositoryImpl
import com.wealthvault.`user-api`.model.FriendData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FormGroupScreenModel(
    private val repository: SocialRepositoryImpl
) : ScreenModel {

    // ... State isLoading และ isSuccess (ใช้ของเดิมที่มีอยู่แล้ว)
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isSuccess = MutableStateFlow(false)
    val isSuccess: StateFlow<Boolean> = _isSuccess.asStateFlow()

    // 🌟 1. เพิ่ม State เก็บรายชื่อเพื่อน
    private val _friends = MutableStateFlow<List<FriendData>>(emptyList())
    val friends: StateFlow<List<FriendData>> = _friends.asStateFlow()

    // 🌟 2. เพิ่มฟังก์ชันดึงเพื่อน
    fun fetchFriends() {
        screenModelScope.launch {
            repository.getAllFriends().onSuccess { data ->
                _friends.value = data
            }
        }
    }

    // ฟังก์ชัน createGroup ของเดิม...
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun createGroup(groupName: String, memberIds: List<String>, imageBytes: ByteArray?) {
        screenModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null // เคลียร์ Error เก่าทิ้งก่อนยิงใหม่

            repository.createGroup(groupName, memberIds, imageBytes).onSuccess {
                println("✅ สร้างกลุ่มสำเร็จ!")
                _isSuccess.value = true
            }.onFailure { error ->
                println("🚨 สร้างกลุ่มไม่สำเร็จ: ${error.message}")
                // 🌟 2. ดันข้อความ Error ไปให้ UI โชว์
                _errorMessage.value = "สร้างกลุ่มไม่สำเร็จ: ${error.message}"
            }

            _isLoading.value = false
        }
    }

    fun updateExistingGroup(
        groupId: String,
        newName: String,
        imageBytes: ByteArray?,
        initialMemberIds: List<String>,
        currentMemberIds: List<String>
    ) {
        screenModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                // 🌟 1. อัปเดตข้อมูลพื้นฐานกลุ่ม (ชื่อและรูป)
                // เรายิง APIPATCH ไปที่เส้นอัปเดตกลุ่มที่คุณ Champ เตรียมไว้
                repository.updateGroup(groupId, newName, imageBytes)

                // 🌟 2. ลอจิกจัดการสมาชิก (Diffing)
                // หาคนที่จะ "เพิ่ม" (มีในลิสต์ปัจจุบัน แต่ไม่มีในลิสต์ตั้งต้น)
                val toAdd = currentMemberIds.filterNot { initialMemberIds.contains(it) }
                toAdd.forEach { targetId ->
                    repository.addGroupMember(groupId, targetId)
                }

                // หาคนที่จะ "ลบ" (มีในลิสต์ตั้งต้น แต่ไม่มีในลิสต์ปัจจุบัน)
                val toRemove = initialMemberIds.filterNot { currentMemberIds.contains(it) }
                toRemove.forEach { targetId ->
                    repository.removeGroupMember(groupId, targetId)
                }

                // ถ้าทำงานครบทุกเส้นแล้ว ให้ถือว่าสำเร็จ
                _isSuccess.value = true

            } catch (e: Exception) {
                // ถ้ามีเส้นใดเส้นหนึ่งพัง ให้พ่น Error ออกมาโชว์ที่ Snackbar
                _errorMessage.value = e.message ?: "เกิดข้อผิดพลาดในการอัปเดตกลุ่ม"
                println("🚨 Update Group Error: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun deleteGroup(groupId: String) {
        screenModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                // 💡 อย่าลืมไปเขียนฟังก์ชัน deleteGroup ใน Repository ให้เรียก API ที่เพิ่งสร้างด้วยนะครับ
                repository.deleteGroup(groupId)

                println("✅ ลบกลุ่มสำเร็จ!")
                _isSuccess.value = true // พอเปลี่ยนเป็น true, LaunchedEffect ใน UI จะสั่ง navigator.pop() อัตโนมัติ

            } catch (e: Exception) {
                println("🚨 ลบกลุ่มไม่สำเร็จ: ${e.message}")
                _errorMessage.value = e.message ?: "ลบกลุ่มไม่สำเร็จ กรุณาลองใหม่อีกครั้ง"
            } finally {
                _isLoading.value = false
            }
        }
    }
}