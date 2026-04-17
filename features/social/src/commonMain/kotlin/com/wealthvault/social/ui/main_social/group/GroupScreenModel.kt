package com.wealthvault.social.ui.main_social.group

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.group_api.model.GetAllGroupData
import com.wealthvault.social.data.SocialRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GroupScreenModel(
    private val repository: SocialRepositoryImpl
) : ScreenModel {

    private val _groups = MutableStateFlow<List<GetAllGroupData>>(emptyList())
    val groups: StateFlow<List<GetAllGroupData>> = _groups.asStateFlow()

    fun fetchGroups() {
        screenModelScope.launch {
            repository.getAllGroups().onSuccess { data ->
                _groups.value = data
            }
        }
    }
}