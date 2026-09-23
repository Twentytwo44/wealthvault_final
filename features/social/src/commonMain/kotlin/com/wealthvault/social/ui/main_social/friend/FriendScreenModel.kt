package com.wealthvault.social.ui.main_social.friend

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.domain.social.SocialRepository
import com.wealthvault.domain.profile.FriendData
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

data class FriendUiData(val friends: List<FriendData> = emptyList())

sealed interface FriendUiAction : UiAction {
    data object Refresh : FriendUiAction
}

sealed interface FriendUiEffect : UiEffect {
    data class ShowError(val error: AppError) : FriendUiEffect
}

class FriendScreenModel(
    private val repository: SocialRepository
) : ScreenModel {

    private val _friends = MutableStateFlow<List<FriendData>>(emptyList())
    val friends: StateFlow<List<FriendData>> = _friends.asStateFlow()

    private val _uiState = MutableStateFlow(UiState(data = FriendUiData()))
    val uiState: StateFlow<UiState<FriendUiData>> = _uiState
    private val _effects = MutableSharedFlow<FriendUiEffect>(extraBufferCapacity = 1)
    val effects = _effects.asSharedFlow()

    private var fetchJob: Job? = null

    fun onAction(action: FriendUiAction) {
        when (action) {
            FriendUiAction.Refresh -> fetchFriends(forceRefresh = true)
        }
    }

    fun fetchFriends(forceRefresh: Boolean = false) {
        if (fetchJob?.isActive == true) return
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        val job = screenModelScope.launch {
            try {
                repository.getAllFriends(force = forceRefresh).onSuccess { data ->
                    _friends.value = data
                    _uiState.value = UiState(data = FriendUiData(data))
                }.onFailure { error ->
                    val appError = error.toAppError()
                    _uiState.value = _uiState.value.copy(isLoading = false, error = appError)
                    _effects.tryEmit(FriendUiEffect.ShowError(appError))
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                val appError = error.toAppError()
                _uiState.value = _uiState.value.copy(isLoading = false, error = appError)
                _effects.tryEmit(FriendUiEffect.ShowError(appError))
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
