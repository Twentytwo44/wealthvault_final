package com.wealthvault.social.ui.manage_shared

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.data_store.TokenStore // 🌟 1. นำเข้า TokenStore
import com.wealthvault.share_api.model.ShareGroupData
import com.wealthvault.social.data.SocialRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull // 🌟 นำเข้า firstOrNull
import kotlinx.coroutines.launch

class SharedAssetManageScreenModel(
    private val repository: SocialRepositoryImpl,
    private val tokenStore: TokenStore // 🌟 2. Inject TokenStore เข้ามาใน Constructor
) : ScreenModel {

    private val _assetList = MutableStateFlow<List<ShareGroupData>>(emptyList())
    val assetList: StateFlow<List<ShareGroupData>> = _assetList.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // 🌟 ลบ mocID ทิ้งไปเลยครับ!

    // 🌟 ฟังก์ชันดึงข้อมูลทรัพย์สินที่แชร์
    fun fetchSharedAssets(targetId: String, isGroup: Boolean) {
        screenModelScope.launch {
            _isLoading.value = true

            // 🌟 3. อ่านค่า User ID จากเครื่อง
            val currentUserId = tokenStore.getUserId.firstOrNull() ?: ""

            if (isGroup) {
                // 🔹 กรณีเป้าหมายคือ Group
                val result = repository.getShareGroupItems(targetId)
                val allItems = result.getOrNull() ?: emptyList()

                // 🌟 4. กรองโดยใช้ ID จริง
                _assetList.value = allItems.filter { it.sharedBy == currentUserId }
            } else {
                // 🔹 กรณีเป้าหมายคือ Friend (เพื่อน)
                val result = repository.getShareFriendItems(targetId)
                val allFriendItems = result.getOrNull() ?: emptyList()

                // 🌟 ทำการแปลง ShareFriendData ให้กลายเป็น ShareGroupData
                val mappedItems = allFriendItems.map { friendData ->
                    ShareGroupData(
                        groupItemId = friendData.groupItemId,
                        sharedBy = friendData.sharedBy,
                        sharedAt = friendData.sharedAt,
                        type = friendData.type,
                        assetDetail = friendData.assetDetail
                    )
                }

                // 🌟 4. กรองโดยใช้ ID จริง
                _assetList.value = mappedItems.filter { it.sharedBy == currentUserId }
            }

            _isLoading.value = false
        }
    }

    // 🌟 ฟังก์ชันยกเลิกการแชร์ (ลบข้อมูล)
    fun unShareAsset(assetId: String, isGroup: Boolean) {
        screenModelScope.launch {
            repository.unShareAsset(assetId, isGroup)
                .onSuccess {
                    // ถ้าลบสำเร็จ ให้เตะ Asset นั้นออกจาก List ทันที (Optimistic Update)
                    _assetList.value = _assetList.value.filterNot { it.groupItemId == assetId }
                }
                .onFailure { error ->
                    println("🚨 ยกเลิกการแชร์ไม่สำเร็จ: ${error.message}")
                }
        }
    }
}