package com.wealthvault.profile.ui

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.profile.data.ProfileRepositoryImpl
import com.wealthvault.`user-api`.model.UserData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileScreenModel(
    private val repository: ProfileRepositoryImpl
) : ScreenModel {

    private val _userState = MutableStateFlow<UserData?>(null)
    val userState = _userState.asStateFlow()

    init {
        fetchUser()
    }

    // 🌟 เอาคำว่า private ออกเพื่อให้ UI สั่ง refresh ได้
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
}