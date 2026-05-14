package com.wealthvault.profile.ui

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.profile.data.ProfileRepositoryImpl
import com.wealthvault.`user-api`.model.UpdateUserDataRequest
import com.wealthvault.`user-api`.model.UserData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditProfileScreenModel(
    private val repository: ProfileRepositoryImpl
) : ScreenModel {
    private val _profileImageByteArray = MutableStateFlow<ByteArray?>(null)
    val profileImageByteArray = _profileImageByteArray.asStateFlow()

    fun setProfileImageByteArray(data: ByteArray?) {
        _profileImageByteArray.value = data
    }

    // ข้อมูล User ล่าสุด
    private val _userState = MutableStateFlow<UserData?>(null)
    val userState = _userState.asStateFlow()

    // 🌟 1. เพิ่ม State สำหรับบอกว่ากำลังดึงข้อมูล User อยู่หรือไม่ (แก้โหลดค้าง)
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    // สถานะการบันทึก
    private val _isSaving = MutableStateFlow(false)
    val isSaving = _isSaving.asStateFlow()

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess = _saveSuccess.asStateFlow()

    init {
        fetchUser() // โหลดครั้งแรกเมื่อสร้าง Model
    }

    // 🌟 ปรับปรุง: ใส่การเปิด/ปิด _isLoading ให้ชัดเจน
    fun fetchUser() {
        screenModelScope.launch {
            _isLoading.value = true // เริ่มหมุนโหลด

            repository.getUser().onSuccess {
                _userState.value = it
                println("✅ EditProfile: Data Fetched Successfully")
            }.onFailure { error ->
                println("🚨 EditProfile: Fetch Failed ${error.message}")
            }

            _isLoading.value = false // 🌟 ดึงเสร็จหรือดึงพัง ก็ต้องสั่งให้หยุดหมุน!
        }
    }

    fun saveProfile(username: String, firstName: String, lastName: String, birthDate: String, phone: String) {
        screenModelScope.launch {
            _isSaving.value = true

            // 1. ดึง ByteArray ที่เก็บไว้ตอน User เลือกรูป
            val currentImage = _profileImageByteArray.value

            val request = UpdateUserDataRequest(
                username = username,
                firstName = firstName,
                lastName = lastName,
                birthday = birthDate,
                phoneNumber = phone,
                profileImage = currentImage // 2. แนบรูปส่งไปด้วย (หรือ null ถ้าไม่ได้เปลี่ยนรูป)
            )

            // ยิง API (ถ้าใน ProfileDataSource/Repository ยังไม่ได้รับ profileImage อย่าลืมไปเติมนะครับ)
            repository.updateUserData(request).onSuccess {
                _saveSuccess.value = true
            }.onFailure { error ->
                println("Save failed : ${error.message}")
            }

            _isSaving.value = false
        }
    }

    // ฟังก์ชันล้างสถานะ เพื่อป้องกันบั๊กเด้งออกเองเวลาเข้าหน้าใหม่
    fun resetSaveState() {
        _saveSuccess.value = false
        _isSaving.value = false
    }
}