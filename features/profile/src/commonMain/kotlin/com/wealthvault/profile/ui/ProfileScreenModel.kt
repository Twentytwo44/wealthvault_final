package com.wealthvault.profile.ui

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.core.architecture.AppError
import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.architecture.UiAction
import com.wealthvault.core.architecture.UiEffect
import com.wealthvault.core.architecture.UiState
import com.wealthvault.core.architecture.toThrowable
import com.wealthvault.core.architecture.toAppError
import com.wealthvault.core.observability.AppLogger
import com.wealthvault.core.observability.platformLogger
import com.wealthvault.domain.profile.CloseFriendData
import com.wealthvault.domain.profile.ProfileRepository
import com.wealthvault.domain.profile.UserData
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.Job
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

data class ProfileUiState(
    val user: UserData? = null,
    val closeFriends: List<CloseFriendData> = emptyList(),
    val isLoading: Boolean = true,
    val error: AppError? = null,
)

sealed interface ProfileUiAction : UiAction {
    data object Refresh : ProfileUiAction
}

sealed interface ProfileUiEffect : UiEffect {
    data class ShowError(val error: AppError) : ProfileUiEffect
}

class ProfileScreenModel(
    private val repository: ProfileRepository,
    private val logger: AppLogger = platformLogger(),
) : ScreenModel {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()
    val appUiState: StateFlow<UiState<ProfileUiState>> = _uiState
        .map { state ->
            UiState(
                data = state,
                isLoading = state.isLoading,
                error = state.error,
            )
        }
        .stateIn(
            screenModelScope,
            SharingStarted.Eagerly,
            UiState(data = ProfileUiState()),
        )

    private val _effects = MutableSharedFlow<ProfileUiEffect>(extraBufferCapacity = 1)
    val effects = _effects.asSharedFlow()

    private var refreshJob: Job? = null

    init {
        refresh(forceRefresh = false)
    }

    fun onAction(action: ProfileUiAction) {
        when (action) {
            ProfileUiAction.Refresh -> refresh(forceRefresh = true)
        }
    }

    /** Compatibility entry point for older routes; work still goes through UDF. */
    fun fetchProfileData() {
        refresh(forceRefresh = false)
    }

    private fun refresh(forceRefresh: Boolean) {
        if (refreshJob?.isActive == true) return
        val job = screenModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                var firstError: AppError? = null
                var user = _uiState.value.user
                var closeFriends = _uiState.value.closeFriends

                when (val result = repository.getUser(force = forceRefresh)) {
                    is AppResult.Success -> user = result.value
                    is AppResult.Failure -> {
                        firstError = result.error
                        logger.warn("Get user profile failed", result.error.toThrowable())
                    }
                }

                when (val result = repository.getCloseFriends(force = forceRefresh)) {
                    is AppResult.Success -> closeFriends = result.value
                    is AppResult.Failure -> {
                        firstError = firstError ?: result.error
                        logger.warn("Get close friends failed", result.error.toThrowable())
                    }
                }

                _uiState.value = ProfileUiState(
                    user = user,
                    closeFriends = closeFriends,
                    isLoading = false,
                    error = firstError,
                )
                firstError?.let { _effects.tryEmit(ProfileUiEffect.ShowError(it)) }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                val appError = error.toAppError()
                logger.warn("Profile refresh failed unexpectedly", error)
                _uiState.update { it.copy(isLoading = false, error = appError) }
                _effects.tryEmit(ProfileUiEffect.ShowError(appError))
            }
        }
        refreshJob = job
        job.invokeOnCompletion { if (refreshJob === job) refreshJob = null }
    }
}
