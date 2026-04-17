package com.wealthvault.social.ui.space

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.social.data.SocialRepositoryImpl
import com.wealthvault.`user-api`.model.FriendData
import com.wealthvault.`user-api`.model.MessageItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FriendSpaceScreenModel(
    private val repository: SocialRepositoryImpl
) : ScreenModel {

    private val _messages = MutableStateFlow<List<MessageItem>>(emptyList())
    val messages: StateFlow<List<MessageItem>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun fetchMessages(friendId: String) {
        screenModelScope.launch {
            _isLoading.value = true
            repository.getFriendMessages(friendId).onSuccess { data ->
                _messages.value = data
            }.onFailure {
                _messages.value = emptyList()
            }
            _isLoading.value = false
        }
    }


}