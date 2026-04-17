package com.wealthvault.social.ui.space

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.group_api.model.GroupMsgData
import com.wealthvault.social.data.SocialRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GroupSpaceScreenModel(
    private val repository: SocialRepositoryImpl
) : ScreenModel {

    private val _messages = MutableStateFlow<List<GroupMsgData>>(emptyList())
    val messages: StateFlow<List<GroupMsgData>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun fetchMessages(groupId: String) {
        screenModelScope.launch {
            _isLoading.value = true
            repository.getGroupMessages(groupId)
                .onSuccess { data -> _messages.value = data }
                .onFailure { _messages.value = emptyList() }
            _isLoading.value = false
        }
    }

    fun grantAccess(groupId: String, targetId: String, itemIds: List<String>) {
        screenModelScope.launch {
            repository.grantAccess(groupId, targetId, itemIds).onSuccess {
                // 🌟 เมื่อบันทึกเสร็จ ให้โหลดข้อความแชทซ้ำอีกรอบ!
                fetchMessages(groupId)
            }
        }
    }
}