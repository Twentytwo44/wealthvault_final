package com.wealthvault.financiallist.ui.shareasset

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.financiallist.data.share.UnshareRepositoryImpl
import com.wealthvault.financiallist.ui.shareasset.model.FriendTargetModel
import com.wealthvault.financiallist.ui.shareasset.model.GroupTargetModel
import com.wealthvault.financiallist.ui.shareasset.model.ShareInfo
import com.wealthvault.financiallist.ui.shareasset.model.ShareTo
import com.wealthvault.financiallist.ui.shareasset.usecase.GetShareAssetUseCase
import com.wealthvault.share_api.model.ShareItemRequest
import com.wealthvault.share_api.model.TargetItem
import com.wealthvault_final.`financial-asset`.data.share.ShareItemRepositoryImpl
// 🌟 นำเข้าคลาส Unshare ของคุณแชมป์ด้วยนะครับ (ถ้าชื่อเปลี่ยนไป ให้ปรับตามของจริงได้เลย)
// import com.wealthvault.share.data.UnshareRepositoryImpl
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ShareAssetState(
    val id: String = "",
    val type: String = "",
    val isLoading: Boolean = false
)

class ShareScreenModel(
    private val getShareAssetUseCase: GetShareAssetUseCase,
    private val shareRepository: ShareItemRepositoryImpl,
    private val unshareRepository: UnshareRepositoryImpl
) : ScreenModel {

    private val _formState = MutableStateFlow(ShareAssetState())
    val formState = _formState.asStateFlow()

    private val _shareData = MutableStateFlow(ShareTo())

    private val _friendState = MutableStateFlow<List<FriendTargetModel>>(emptyList())
    val friendState = _friendState.asStateFlow()

    private val _groupState = MutableStateFlow<List<GroupTargetModel>>(emptyList())
    val groupState = _groupState.asStateFlow()

    fun initData(id: String, type: String) {
        _formState.update { it.copy(id = id, type = type) }
        fetchData(id, type)
    }

    fun initShareData(shareTo: ShareTo) {
        _shareData.update {
            it.copy(
                friend = shareTo.friend,
                email = shareTo.email,
                group = shareTo.group,
            )
        }
    }

    private fun fetchData(id: String, type: String) {
        screenModelScope.launch {
            _formState.update { it.copy(isLoading = true) }

            val result = getShareAssetUseCase(id, type)

            result.onSuccess { data ->
                _friendState.value = data.mappedFriends
                _groupState.value = data.mappedGroups
                println("✅ All Data Loaded Successfully")
            }.onFailure { error ->
                println("❌ Failed to get data: ${error.message}")
            }

            _formState.update { it.copy(isLoading = false) }
        }
    }


    fun prepareUnshareItem(item: ShareInfo, currentAssetId: String, onReady: (ShareInfo) -> Unit) {
        screenModelScope.launch {
            try {
                var foundSharedItemId = ""

                if (item.typeData == "F") {
                    // ไปดึงรายการที่แชร์ให้เพื่อนคนนี้
                    val res = unshareRepository.getFriendItems(item.userId).getOrNull()
                    // ค้นหาชิ้นที่ Asset ID ตรงกับหน้าปัจจุบัน
                    val match = res?.data?.find { it.assetDetail?.id == currentAssetId }
                    foundSharedItemId = match?.groupItemId ?: ""
                } else if (item.typeData == "G") {
                    // ไปดึงรายการที่แชร์ให้กลุ่มนี้
                    val res = unshareRepository.getGroupItems(item.userId).getOrNull()
                    val match = res?.data?.find { it.assetDetail?.id == currentAssetId }
                    foundSharedItemId = match?.groupItemId ?: ""
                }

                if (foundSharedItemId.isNotEmpty()) {
                    // 🌟 ถ้าเจอ ให้เอา sharedItemId ยัดใส่ Object แล้วส่งกลับไป
                    val preparedItem = item.copy(sharedItemId = foundSharedItemId)
                    println("🔍 เตรียมพร้อมลบ! เจอ shared_item_id: $foundSharedItemId")
                    onReady(preparedItem)
                } else {
                    println("❌ หา shared_item_id ไม่เจอ (อาจจะลบไปแล้วหรือ API ผิด)")
                }
            } catch (e: Exception) {
                println("❌ Error finding shared_item_id: ${e.message}")
            }
        }
    }

    fun submitShare(id: String, type: String, unshareList: List<ShareInfo>, onSuccess: () -> Unit) {
        val shareToData = _shareData.value
        screenModelScope.launch {
            _formState.update { it.copy(isLoading = true) }

            try {
                // 🌟 1. ยิง API ยกเลิกการแชร์ (Unshare) ให้ครบทุกคนในถังขยะ
                if (unshareList.isNotEmpty()) {
                    val unshareJobs = unshareList.map { item ->
                        async {
                            try {
                                // 🚨 ใช้งาน item.sharedItemId ที่เราแอบไปโหลดมา!
                                if (item.typeData == "F" && item.sharedItemId.isNotBlank()) {
                                    unshareRepository.unshareFriend(item.sharedItemId)
                                } else if (item.typeData == "G" && item.sharedItemId.isNotBlank()) {
                                    unshareRepository.unshareGroup(item.sharedItemId)
                                }
                            } catch (e: Exception) {
                                println("❌ Unshare Failed: ${e.message}")
                            }
                        }
                    }
                    unshareJobs.awaitAll()
                }

                // 🌟 2. ยิง API อัปเดต/เพิ่ม คนที่แชร์ใหม่
                val requestShareItem = ShareItemRequest(
                    itemIds = id,
                    itemTypes = type,
                    emails = shareToData.email.map { TargetItem(id = it.userId, shareAt = it.apiDate) },
                    friends = shareToData.friend.map { TargetItem(id = it.userId, shareAt = it.apiDate) },
                    groups = shareToData.group.map { TargetItem(id = it.userId, shareAt = it.apiDate) }
                )

                val shareResult = shareRepository.shareItem(requestShareItem)

                if (shareResult.isSuccess) {
                    println("✅ [ShareScreenModel] Share & Unshare successful")
                    onSuccess()
                } else {
                    println("❌ [ShareScreenModel] Share failed: ${shareResult.exceptionOrNull()?.message}")
                }
            } catch (e: Exception) {
                println("❌ [ShareScreenModel] Error: ${e.message}")
            } finally {
                _formState.update { it.copy(isLoading = false) }
            }
        }
    }
}