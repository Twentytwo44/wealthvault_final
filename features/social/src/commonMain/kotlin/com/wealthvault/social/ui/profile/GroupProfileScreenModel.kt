package com.wealthvault.social.ui.profile

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.domain.social.GroupData
import com.wealthvault.domain.social.GroupMember
import com.wealthvault.domain.social.SocialRepository
import com.wealthvault.core.architecture.AppError
import com.wealthvault.core.architecture.UiAction
import com.wealthvault.core.architecture.UiEffect
import com.wealthvault.core.architecture.UiState
import com.wealthvault.core.architecture.toAppError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.Job
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

data class GroupProfileUiData(
    val group: GroupData? = null,
    val members: List<GroupMember> = emptyList(),
    val leaveSuccess: Boolean = false,
)

sealed interface GroupProfileUiAction : UiAction {
    data class Refresh(val groupId: String) : GroupProfileUiAction
    data class Leave(val groupId: String) : GroupProfileUiAction
}

sealed interface GroupProfileUiEffect : UiEffect {
    data object Saved : GroupProfileUiEffect
    data class ShowError(val error: AppError) : GroupProfileUiEffect
}

class GroupProfileScreenModel(
    private val repository: SocialRepository
) : ScreenModel {

    private val _groupData = MutableStateFlow<GroupData?>(null)
    val groupData = _groupData.asStateFlow()

    // 🌟 สร้าง State มารับรายชื่อสมาชิก
    private val _members = MutableStateFlow<List<GroupMember>>(emptyList())
    val members = _members.asStateFlow()

    private var fetchJob: Job? = null
    private var leaveJob: Job? = null

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    // 🌟 โหลดทั้งคู่รวดเดียว
    fun fetchGroupData(groupId: String) {
        if (fetchJob?.isActive == true) return
        val job = screenModelScope.launch {
            _isLoading.value = true
            syncUiState(isLoading = true, error = null)
            try {
                // โหลด 2 อย่าง
                val detailResult = repository.getGroupDetail(groupId)
                val membersResult = repository.getGroupMembers(groupId)

                _groupData.value = detailResult.getOrNull()
                _members.value = membersResult.getOrNull() ?: emptyList()

                val failure = detailResult.exceptionOrNull() ?: membersResult.exceptionOrNull()
                if (failure != null) {
                    val appError = failure.toAppError()
                    syncUiState(isLoading = false, error = appError)
                    _effects.tryEmit(GroupProfileUiEffect.ShowError(appError))
                } else {
                    syncUiState(isLoading = false, error = null)
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                val appError = error.toAppError()
                syncUiState(isLoading = false, error = appError)
                _effects.tryEmit(GroupProfileUiEffect.ShowError(appError))
            } finally {
                _isLoading.value = false
            }
        }
        fetchJob = job
        job.invokeOnCompletion {
            if (fetchJob === job) fetchJob = null
        }
    }
    private val _leaveSuccess = MutableStateFlow(false)
    val leaveSuccess = _leaveSuccess.asStateFlow()

    private val _uiState = MutableStateFlow(UiState(data = GroupProfileUiData(), isLoading = true))
    val uiState: StateFlow<UiState<GroupProfileUiData>> = _uiState.asStateFlow()
    private val _effects = MutableSharedFlow<GroupProfileUiEffect>(extraBufferCapacity = 1)
    val effects = _effects.asSharedFlow()

    fun onAction(action: GroupProfileUiAction) {
        when (action) {
            is GroupProfileUiAction.Refresh -> fetchGroupData(action.groupId)
            is GroupProfileUiAction.Leave -> leaveGroup(action.groupId)
        }
    }

    private fun syncUiState(isLoading: Boolean = _isLoading.value, error: AppError? = _uiState.value.error) {
        _uiState.value = UiState(
            data = GroupProfileUiData(_groupData.value, _members.value, _leaveSuccess.value),
            isLoading = isLoading,
            error = error,
        )
    }

    fun leaveGroup(groupId: String) {
        if (_isLoading.value || leaveJob?.isActive == true) return
        _isLoading.value = true
        val job = screenModelScope.launch {
            try {
                val result = repository.leaveGroup(groupId)
                result.onSuccess {
                    _leaveSuccess.value = true
                    syncUiState(isLoading = false, error = null)
                    _effects.tryEmit(GroupProfileUiEffect.Saved)
                }.onFailure { error ->
                    val appError = error.toAppError()
                    syncUiState(isLoading = false, error = appError)
                    _effects.tryEmit(GroupProfileUiEffect.ShowError(appError))
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                val appError = error.toAppError()
                syncUiState(isLoading = false, error = appError)
                _effects.tryEmit(GroupProfileUiEffect.ShowError(appError))
            } finally {
                _isLoading.value = false
            }
        }
        leaveJob = job
        job.invokeOnCompletion { if (leaveJob === job) leaveJob = null }
    }
}
