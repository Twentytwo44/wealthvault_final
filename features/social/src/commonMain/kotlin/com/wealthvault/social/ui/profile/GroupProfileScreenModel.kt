package com.wealthvault.social.ui.profile

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.group_api.model.GroupData
import com.wealthvault.group_api.model.GroupMemberItem // 🌟 Import ตัวใหม่มา
import com.wealthvault.social.data.SocialRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GroupProfileScreenModel(
    private val repository: SocialRepositoryImpl
) : ScreenModel {

    private val _groupData = MutableStateFlow<GroupData?>(null)
    val groupData = _groupData.asStateFlow()

    // 🌟 สร้าง State มารับรายชื่อสมาชิก
    private val _members = MutableStateFlow<List<GroupMemberItem>>(emptyList())
    val members = _members.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    // 🌟 โหลดทั้งคู่รวดเดียว
    fun fetchGroupData(groupId: String) {
        screenModelScope.launch {
            _isLoading.value = true

            // โหลด 2 อย่าง
            val detailResult = repository.getGroupDetail(groupId)
            val membersResult = repository.getGroupMembers(groupId)

            _groupData.value = detailResult.getOrNull()
            _members.value = membersResult.getOrNull() ?: emptyList()

            _isLoading.value = false
        }
    }
    private val _leaveSuccess = MutableStateFlow(false)
    val leaveSuccess = _leaveSuccess.asStateFlow()

    fun leaveGroup(groupId: String) {
        screenModelScope.launch {
            _isLoading.value = true
            val result = repository.leaveGroup(groupId)
            result.onSuccess {
                _leaveSuccess.value = true
            }.onFailure {
            }
            _isLoading.value = false
        }
    }
}