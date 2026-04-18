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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class ShareAssetState(
        val id: String,
        val type: String,
        val isLoading: Boolean
)
class ShareScreenModel(
    private val getShareAssetUseCase: GetShareAssetUseCase,
    private val shareRepository: ShareItemRepositoryImpl
) : ScreenModel {

    private val _formState = MutableStateFlow(ShareAssetState("", "", false))
    val formState = _formState.asStateFlow()

    private val _shareData = MutableStateFlow(ShareTo(
            friend = emptyList(),
            email = emptyList(),
            group = emptyList(),

        ))

    // 💡 1. เปลี่ยนชนิดข้อมูลเป็น UI Model ที่มาจาก UseCase
    private val _friendState = MutableStateFlow<List<FriendTargetModel>>(emptyList())
    val friendState = _friendState.asStateFlow()

    // 💡 2. เปลี่ยนชนิดข้อมูล Group เป็น UI Model
    private val _groupState = MutableStateFlow<List<GroupTargetModel>>(emptyList())
    val groupState = _groupState.asStateFlow()

    // 💡 3. ลบ _shareTargetsState ออกได้เลย เพราะ UI ไม่ต้องใช้ของดิบแล้ว

    fun initData(id: String, type: String) {
//        _formState.update { it.copy(id = id, type = type) }

        println("✅ State Updated: ${_formState.value}")

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
        println("✅ State Updated: ${_formState.value}")

    }



    private fun fetchData(id: String, type: String) {
        screenModelScope.launch {
            _formState.update { it.copy(isLoading = true) }

            val result = getShareAssetUseCase(id, type)

            result.onSuccess { data ->
                // 💡 4. โยนของที่ Map เสร็จแล้วใส่ State ให้ UI เอาไปวาดได้เลย
                _friendState.value = data.mappedFriends
                _groupState.value = data.mappedGroups

                println("✅ All Data Loaded Successfully")
            }.onFailure { error ->
                println("❌ Failed to get data: ${error.message}")
            }

            _formState.update { it.copy(isLoading = false) }
        }
    }


    fun submitShare(id: String,type: String) {
        val shareToData =  _shareData.value ?: return
        screenModelScope.launch {

                    delay(10000)
                    // --- ขั้นตอนที่ 2: เตรียมข้อมูลเพื่อ Share โดยใช้ ID ที่เพิ่งได้มา ---
                    val requestShareItem = ShareItemRequest(
                        itemIds = id, // 👈 ใส่ ID ที่ได้จากขั้นตอนที่ 1
                        itemTypes = type,
                        emails = shareToData.email.map {
                            TargetItem(
                                id = it.name,
                                shareAt = it.date
                            )
                        },
                        friends = shareToData.friend.map {
                            TargetItem(
                                id = it.userId,
                                shareAt = it.date
                            )
                        },
                        groups = shareToData.group.map {
                            TargetItem(
                                id = it.userId,
                                shareAt = it.date
                            )
                        }
                    )

                    // --- ขั้นตอนที่ 3: ยิง API แชร์ทรัพย์สิน ---
                    val shareResult = shareRepository.shareItem(requestShareItem)
                    println(" [EditShareAssetScreenModel] Share result: $shareResult")

        }
    }
}
