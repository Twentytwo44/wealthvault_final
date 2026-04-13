package com.wealthvault_final.`financial-asset`.ui.share

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.group_api.model.GetAllGroupData
import com.wealthvault.`user-api`.model.FriendData
import com.wealthvault_final.`financial-asset`.data.friend.FriendRepositoryImpl
import com.wealthvault_final.`financial-asset`.data.group.GroupRepositoryImpl
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class ShareAssetState<T>(
    val request: T? = null,
    val isLoading: Boolean = false
)
class ShareAssetScreenModel<T>(
    private val friendRepository: FriendRepositoryImpl,
    private val groupRepository: GroupRepositoryImpl,
) : ScreenModel {

    private val _formState = MutableStateFlow(ShareAssetState<T>())
    val formState = _formState.asStateFlow() // อย่าลืมสร้างตัวแปร val ให้ UI อ่านค่าได้ด้วยนะครับ

    private val _friendState = MutableStateFlow<List<FriendData>>(emptyList())
    val friendState = _friendState.asStateFlow()

    private val _groupState = MutableStateFlow<List<GetAllGroupData>>(emptyList())
    val groupState = _groupState.asStateFlow()

    init {
        fetchData()
    }

    fun initData(request: T?) {
        _formState.update { it.copy(request = request) }
        println("✅ State Updated: ${_formState.value}")
    }



    private fun fetchData() {
        screenModelScope.launch {
            _formState.update { it.copy(isLoading = true) }

            val friendDeferred = async { friendRepository.getFriend() }
            val groupDeferred = async { groupRepository.getAllGroup() } // เรียกฟังก์ชันดึง Group

            // รอรับผลลัพธ์จากทั้ง 2 API
            val friendResult = friendDeferred.await()
            val groupResult = groupDeferred.await()


            friendResult.onSuccess { friendData ->
                _friendState.value = friendData
                println("✅ Friend Data: ${friendData}")
            }.onFailure { error ->
                println("❌ Failed to get friends: ${error.message}")
            }


            groupResult.onSuccess { groupData ->
                _groupState.value = groupData
                println("✅ Group Data: $groupData")

            }.onFailure { error ->
                println("❌ Failed to get groups: ${error.message}")
            }

            _formState.update { it.copy(isLoading = false) }
        }
    }
}
