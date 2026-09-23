package com.wealthvault.social.ui.space

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.domain.social.SocialRepository
import com.wealthvault.domain.profile.FriendData
import com.wealthvault.domain.social.MessageItem
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

data class FriendSpaceUiData(val messages: List<MessageItem> = emptyList())

sealed interface FriendSpaceUiAction : UiAction {
    data class Refresh(val friendId: String) : FriendSpaceUiAction
}

sealed interface FriendSpaceUiEffect : UiEffect {
    data class ShowError(val error: AppError) : FriendSpaceUiEffect
}

class FriendSpaceScreenModel(
    private val repository: SocialRepository
) : ScreenModel {

    private val _messages = MutableStateFlow<List<MessageItem>>(emptyList())
    val messages: StateFlow<List<MessageItem>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _uiState = MutableStateFlow(UiState(data = FriendSpaceUiData(), isLoading = true))
    val uiState: StateFlow<UiState<FriendSpaceUiData>> = _uiState.asStateFlow()
    private val _effects = MutableSharedFlow<FriendSpaceUiEffect>(extraBufferCapacity = 1)
    val effects = _effects.asSharedFlow()

    private var fetchJob: Job? = null

    fun onAction(action: FriendSpaceUiAction) {
        when (action) {
            is FriendSpaceUiAction.Refresh -> fetchMessages(action.friendId)
        }
    }

    fun fetchMessages(friendId: String) {
        if (fetchJob?.isActive == true) return
        val job = screenModelScope.launch {
            _isLoading.value = true
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                repository.getFriendMessages(friendId).onSuccess { data ->
                    _messages.value = data
                    _uiState.value = UiState(data = FriendSpaceUiData(data))
                }.onFailure { error ->
                    _messages.value = emptyList()
                    val appError = error.toAppError()
                    _uiState.value = UiState(data = FriendSpaceUiData(), error = appError)
                    _effects.tryEmit(FriendSpaceUiEffect.ShowError(appError))
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                val appError = error.toAppError()
                _messages.value = emptyList()
                _uiState.value = UiState(data = FriendSpaceUiData(), error = appError)
                _effects.tryEmit(FriendSpaceUiEffect.ShowError(appError))
            } finally {
                _isLoading.value = false
            }
        }
        fetchJob = job
        job.invokeOnCompletion {
            if (fetchJob === job) fetchJob = null
        }
    }


}
