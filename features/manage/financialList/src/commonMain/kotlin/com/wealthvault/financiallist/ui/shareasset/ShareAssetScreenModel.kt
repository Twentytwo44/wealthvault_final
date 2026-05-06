package com.wealthvault.financiallist.ui.shareasset

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.financiallist.ui.shareasset.model.FriendTargetModel
import com.wealthvault.financiallist.ui.shareasset.model.GroupTargetModel
import com.wealthvault.financiallist.ui.shareasset.model.ShareTo
import com.wealthvault.financiallist.ui.shareasset.usecase.GetShareAssetUseCase
import com.wealthvault.share_api.model.ShareItemRequest
import com.wealthvault.share_api.model.TargetItem
import com.wealthvault_final.`financial-asset`.data.share.ShareItemRepositoryImpl
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
    private val shareRepository: ShareItemRepositoryImpl
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

    // 🌟 ปรับปรุงให้รองรับ Callback onSuccess
    fun submitShare(id: String, type: String, onSuccess: () -> Unit) {
        val shareToData = _shareData.value
        screenModelScope.launch {
            _formState.update { it.copy(isLoading = true) }

            // --- เตรียมข้อมูลเพื่อ Share โดยใช้ apiDate ที่เป็นรูปแบบ YYYY-MM-DD ---
            val requestShareItem = ShareItemRequest(
                itemIds = id,
                itemTypes = type,
                emails = shareToData.email.map {
                    TargetItem(
                        id = it.userId, // ใน Email userId คือตัว email string
                        shareAt = it.apiDate // 🌟 ใช้ apiDate แทน date
                    )
                },
                friends = shareToData.friend.map {
                    TargetItem(
                        id = it.userId,
                        shareAt = it.apiDate // 🌟 ใช้ apiDate แทน date
                    )
                },
                groups = shareToData.group.map {
                    TargetItem(
                        id = it.userId,
                        shareAt = it.apiDate // 🌟 ใช้ apiDate แทน date
                    )
                }
            )

            // --- ยิง API แชร์ทรัพย์สิน ---
            val shareResult = shareRepository.shareItem(requestShareItem)

            if (shareResult.isSuccess) {
                println("✅ [ShareScreenModel] Share successful")
                onSuccess() // 🌟 เรียกใช้งาน Callback เมื่อสำเร็จเพื่อ Navigate กลับ
            } else {
                println("❌ [ShareScreenModel] Share failed: ${shareResult.exceptionOrNull()?.message}")
            }

            _formState.update { it.copy(isLoading = false) }
        }
    }
}