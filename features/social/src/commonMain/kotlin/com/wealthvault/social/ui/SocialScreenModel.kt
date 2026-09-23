package com.wealthvault.social.ui

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.core.architecture.AppError
import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.architecture.UiAction
import com.wealthvault.core.architecture.UiEffect
import com.wealthvault.core.architecture.UiState
import com.wealthvault.core.architecture.toAppError
import com.wealthvault.domain.social.SocialRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.Job
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

data class SocialUiData(
    val hasPendingRequest: Boolean = false,
)

sealed interface SocialUiAction : UiAction {
    data object RefreshPendingBadge : SocialUiAction
}

sealed interface SocialUiEffect : UiEffect {
    data class ShowError(val error: AppError) : SocialUiEffect
}

class SocialScreenModel(
    private val repository: SocialRepository
) : ScreenModel {

    private val _uiState = MutableStateFlow<UiState<SocialUiData>>(
        UiState(data = SocialUiData(), isLoading = true),
    )
    val uiState: StateFlow<UiState<SocialUiData>> = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<SocialUiEffect>(extraBufferCapacity = 1)
    val effects = _effects.asSharedFlow()

    // 🌟 สร้าง State ไว้เก็บค่าว่ามีคำขอค้างอยู่ไหม
    private val _hasPendingRequest = MutableStateFlow(false)
    val hasPendingRequest = _hasPendingRequest.asStateFlow()

    private var pendingJob: Job? = null

    fun onAction(action: SocialUiAction) {
        when (action) {
            SocialUiAction.RefreshPendingBadge -> fetchPendingFriendsBadge(forceRefresh = true)
        }
    }

    fun fetchPendingFriendsBadge(forceRefresh: Boolean = false) {
        if (pendingJob?.isActive == true) return
        _uiState.update { it.copy(isLoading = true, error = null) }
        val job = screenModelScope.launch {
            try {
                // The repository owns the TTL; explicit refresh bypasses it.
                when (val result = repository.getPendingFriends(force = forceRefresh)) {
                    is AppResult.Success -> {
                        val hasPending = result.value.isNotEmpty()
                        _hasPendingRequest.value = hasPending
                        _uiState.value = UiState(
                            data = SocialUiData(hasPendingRequest = hasPending),
                            isLoading = false,
                        )
                    }
                    is AppResult.Failure -> {
                        _hasPendingRequest.value = false
                        _uiState.value = UiState(
                            data = SocialUiData(hasPendingRequest = false),
                            isLoading = false,
                            error = result.error,
                        )
                        _effects.tryEmit(SocialUiEffect.ShowError(result.error))
                    }
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                val appError = error.toAppError()
                _uiState.value = UiState(
                    data = SocialUiData(hasPendingRequest = _hasPendingRequest.value),
                    isLoading = false,
                    error = appError,
                )
                _effects.tryEmit(SocialUiEffect.ShowError(appError))
            }
        }
        pendingJob = job
        job.invokeOnCompletion {
            if (pendingJob === job) pendingJob = null
        }
    }
}
