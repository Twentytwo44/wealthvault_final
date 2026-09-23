package com.wealthvault.social.ui.main_social.group

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.domain.social.GroupSummary
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

data class GroupUiData(val groups: List<GroupSummary> = emptyList())

sealed interface GroupUiAction : UiAction {
    data object Refresh : GroupUiAction
}

sealed interface GroupUiEffect : UiEffect {
    data class ShowError(val error: AppError) : GroupUiEffect
}

class GroupScreenModel(
    private val repository: SocialRepository
) : ScreenModel {

    private val _groups = MutableStateFlow<List<GroupSummary>>(emptyList())
    val groups: StateFlow<List<GroupSummary>> = _groups.asStateFlow()

    private val _uiState = MutableStateFlow(UiState(data = GroupUiData()))
    val uiState: StateFlow<UiState<GroupUiData>> = _uiState
    private val _effects = MutableSharedFlow<GroupUiEffect>(extraBufferCapacity = 1)
    val effects = _effects.asSharedFlow()

    private var fetchJob: Job? = null

    fun onAction(action: GroupUiAction) {
        when (action) {
            GroupUiAction.Refresh -> fetchGroups(forceRefresh = true)
        }
    }

    fun fetchGroups(forceRefresh: Boolean = false) {
        if (fetchJob?.isActive == true) return
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        val job = screenModelScope.launch {
            try {
                repository.getAllGroups(force = forceRefresh).onSuccess { data ->
                    _groups.value = data
                    _uiState.value = UiState(data = GroupUiData(data))
                }.onFailure { error ->
                    val appError = error.toAppError()
                    _uiState.value = _uiState.value.copy(isLoading = false, error = appError)
                    _effects.tryEmit(GroupUiEffect.ShowError(appError))
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                val appError = error.toAppError()
                _uiState.value = _uiState.value.copy(isLoading = false, error = appError)
                _effects.tryEmit(GroupUiEffect.ShowError(appError))
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
        fetchJob = job
        job.invokeOnCompletion {
            if (fetchJob === job) fetchJob = null
        }
    }
}
