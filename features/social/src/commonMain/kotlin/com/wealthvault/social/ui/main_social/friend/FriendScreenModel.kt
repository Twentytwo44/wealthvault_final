package com.wealthvault.social.ui.main_social.friend

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.social.data.SocialRepositoryImpl
import com.wealthvault.`user-api`.model.FriendData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FriendScreenModel(
    private val repository: SocialRepositoryImpl
) : ScreenModel {

    private val _friends = MutableStateFlow<List<FriendData>>(emptyList())
    val friends: StateFlow<List<FriendData>> = _friends.asStateFlow()

    fun fetchFriends() {
        screenModelScope.launch {
            repository.getAllFriends().onSuccess { data ->
                _friends.value = data
            }
        }
    }
}