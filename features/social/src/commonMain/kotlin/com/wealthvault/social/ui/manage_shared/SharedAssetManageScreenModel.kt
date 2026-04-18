package com.wealthvault.social.ui.manage_shared

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.share_api.model.ShareGroupData
import com.wealthvault.social.data.SocialRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SharedAssetManageScreenModel(
    private val repository: SocialRepositoryImpl
) : ScreenModel {

    private val _assetList = MutableStateFlow<List<ShareGroupData>>(emptyList())
    val assetList: StateFlow<List<ShareGroupData>> = _assetList.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val mocID = "844c4180-4c6a-438e-bfd3-0e78a24ec1b1"

    // 🌟 1. ฟังก์ชันดึงข้อมูลทรัพย์สินที่แชร์
    fun fetchSharedAssets(targetId: String, isGroup: Boolean) {
        screenModelScope.launch {
            _isLoading.value = true

            // 💡 อนาคตถ้ามี API getShareFriendItems ก็สามารถเอา isGroup มาเช็ค if/else ตรงนี้ได้ครับ
            // แต่ตอนนี้ใช้ getShareGroupItems ไปก่อนตามโค้ดเดิมครับ
            val result = repository.getShareGroupItems(targetId)
            val allItems = result.getOrNull() ?: emptyList()

            _assetList.value = allItems.filter { it.sharedBy == mocID }
            _isLoading.value = false
        }
    }

    // 🌟 2. ฟังก์ชันยกเลิกการแชร์ (ลบข้อมูล)
    fun unShareAsset(assetId: String, isGroup: Boolean) {
        screenModelScope.launch {
            repository.unShareAsset(assetId, isGroup)
                .onSuccess {
                    // 🌟 ถ้าลบสำเร็จ ให้เตะ Asset นั้นออกจาก List ทันที (Optimistic Update)
                    _assetList.value = _assetList.value.filterNot { it.groupItemId == assetId }
                }
                .onFailure { error ->
                    println("🚨 ยกเลิกการแชร์ไม่สำเร็จ: ${error.message}")
                }
        }
    }
}