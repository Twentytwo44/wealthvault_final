package com.wealthvault.profile.ui

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.profile.data.ProfileRepositoryImpl
import com.wealthvault.`user-api`.model.CloseFriendData
import com.wealthvault.`user-api`.model.UserData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileScreenModel(
    private val repository: ProfileRepositoryImpl
) : ScreenModel {
    private val _closeFriends = MutableStateFlow<List<CloseFriendData>>(emptyList())
    val closeFriends = _closeFriends.asStateFlow()
    private val _userState = MutableStateFlow<UserData?>(null)
    val userState = _userState.asStateFlow()

    init {
        fetchUser()
    }

    fun fetchUser() {
        screenModelScope.launch {
            val result = repository.getUser()
            result.onSuccess { userData ->
                _userState.value = userData
            }.onFailure { error ->
                println("Failed to get user: ${error.message}")
            }
        }
    }

    fun fetchCloseFriends() {
        screenModelScope.launch {
            repository.getCloseFriends()
                .onSuccess { friends ->
                    println("✅ [API SUCCESS] Close Friends Data: ${friends.size} คน")
                    _closeFriends.value = friends
                }
                .onFailure { error ->
                    println("❌ [API ERROR] Close Friends พังเพราะ: ${error.message}")
                }
        }
    }
}