package com.wealthvault.social.ui.manage_shared

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.share_api.model.ItemToShareData
import com.wealthvault.share_api.model.ShareItemRequest
import com.wealthvault.share_api.model.TargetItem
import com.wealthvault.social.data.SocialRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SharedAssetScreenModel(
    private val repository: SocialRepositoryImpl
) : ScreenModel {

    private val _assetList = MutableStateFlow<List<ItemToShareData>>(emptyList())
    val assetList: StateFlow<List<ItemToShareData>> = _assetList.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isShareSuccess = MutableStateFlow(false)
    val isShareSuccess: StateFlow<Boolean> = _isShareSuccess.asStateFlow()

    // 🌟 ฟังก์ชันโหลดรายการสินทรัพย์ที่แชร์ได้
    // ใน SharedAssetScreenModel.kt

    fun fetchItemsToShare(targetId: String, isGroup: Boolean) {
        screenModelScope.launch {
            _isLoading.value = true

            // 🌟 ส่ง targetId และ isGroup ไปพร้อมกัน
            repository.getItemsToShare(targetId, isGroup).onSuccess { list ->
                _assetList.value = list
            }.onFailure { error ->
                _errorMessage.value = error.message
            }

            _isLoading.value = false
        }
    }
    // 🌟 ฟังก์ชันยิง API เพื่อบันทึกการแชร์ (เดี๋ยวรอคุณ Champ สร้าง API ฝั่งบันทึกมาเชื่อมที่นี่ครับ)
    fun submitShareAssets(targetId: String, selectedIds: List<String>) {
        screenModelScope.launch {
            _isLoading.value = true
            // TODO: เรียก repository.submitShareAssets(...)
            // result.onSuccess { _isShareSuccess.value = true }
            _isLoading.value = false
        }
    }

    // 🌟 ฟังก์ชันบันทึกการแชร์ (แบบลูปส่งทีละรายการ)
    fun submitShareAssets(targetId: String, isGroup: Boolean, selectedIds: List<String>) {
        screenModelScope.launch {
            _isLoading.value = true
            _isShareSuccess.value = false
            _errorMessage.value = null

            // 1. ค้นหาสินทรัพย์ที่ถูกเลือกจาก _assetList ปัจจุบัน
            val selectedAssets = _assetList.value.filter { it.id in selectedIds }

            // 2. จัดการเป้าหมาย (ส่งให้เพื่อน หรือ ส่งให้กลุ่ม เดิมเสมอ)
            val targetList = listOf(TargetItem(id = targetId))

            var isAllSuccess = true
            var lastErrorMsg: String? = null

            // 3. 🌟 ลูปสินทรัพย์ที่เลือก เพื่อยิง API ทีละตัว
            for (asset in selectedAssets) {
                // ข้ามตัวที่ไม่มี ID หรือ Type
                val currentId = asset.id ?: continue
                val currentType = asset.type ?: continue

                // สร้าง Request สำหรับ "1 สินทรัพย์" ต่อ "1 กลุ่ม/เพื่อน"
                val request = ShareItemRequest(
                    itemIds = currentId,     // ส่ง ID ทีละ 1 ชิ้น
                    itemTypes = currentType, // ส่ง Type ของชิ้นนั้น
                    friends = if (!isGroup) targetList else null,
                    groups = if (isGroup) targetList else null,
                    emails = null
                )

                // 4. ยิง API ทีละรายการ และรอผลลัพธ์
                val result = repository.submitShareItems(request)

                // ถ้ามีตัวไหนยิงไม่ผ่าน ให้บันทึก Error ไว้
                if (result.isFailure) {
                    isAllSuccess = false
                    lastErrorMsg = result.exceptionOrNull()?.message
                    // 💡 ถ้าอยากให้ตัวที่เหลือยิงต่อให้จบ ให้ลบคำว่า break ออกครับ
                    // แต่ถ้าอยากให้หยุดทันทีเมื่อเจอ Error ให้ใส่ break ไว้แบบนี้ครับ
                    break
                }
            }

            // 5. สรุปผลลัพธ์หลังจากลูปจบ
            if (isAllSuccess) {
                _isShareSuccess.value = true
            } else {
                _isShareSuccess.value = false
                _errorMessage.value = lastErrorMsg ?: "เกิดข้อผิดพลาดในการแชร์บางรายการ"
            }

            _isLoading.value = false
        }
    }
}