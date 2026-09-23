package com.wealthvault.dashboard.ui

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.architecture.UiAction
import com.wealthvault.core.architecture.UiEffect
import com.wealthvault.core.architecture.UiState
import com.wealthvault.core.architecture.toAppError
import com.wealthvault.core.notification.NotificationBadgeProvider
import com.wealthvault.domain.portfolio.DashboardRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DashboardScreenModel(
    private val repository: DashboardRepository,
    private val notificationBadgeProvider: NotificationBadgeProvider,
) : ScreenModel {

    private val _uiState = MutableStateFlow(DashboardUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    /** Canonical state envelope used by new routes; [uiState] is retained for old callers. */
    val appUiState: StateFlow<UiState<DashboardUiState>> = uiState
        .map { state ->
            UiState(
                data = state,
                isLoading = state.isLoading,
                isStale = state.freshness != com.wealthvault.core.architecture.CacheFreshness.Fresh,
                error = state.error,
            )
        }
        .stateIn(screenModelScope, SharingStarted.Eagerly, UiState(data = DashboardUiState(isLoading = true)))

    private val _effects = MutableSharedFlow<DashboardUiEffect>(extraBufferCapacity = 1)
    val effects = _effects.asSharedFlow()

    private var refreshJob: Job? = null

    init {
        refresh()
    }

    /** Refresh is explicit and deduplicated; the screen no longer refetches on every lifecycle event. */
    fun onAction(action: DashboardUiAction) {
        when (action) {
            DashboardUiAction.Refresh -> refresh(forceRefresh = true)
        }
    }

    fun refresh(forceRefresh: Boolean = false) {
        if (refreshJob?.isActive == true) return

        val job = screenModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }

                val dashboardResult = repository.getDashboardData(forceRefresh = forceRefresh)
                val dashboardSnapshot = when (dashboardResult) {
                    is AppResult.Success -> dashboardResult.value
                    is AppResult.Failure -> {
                        _uiState.update { it.copy(isLoading = false, error = dashboardResult.error) }
                        _effects.tryEmit(DashboardUiEffect.ShowError(dashboardResult.error))
                        return@launch
                    }
                }
                val hasUnread = when (val badgeResult = notificationBadgeProvider.hasUnread()) {
                    is AppResult.Success -> badgeResult.value
                    is AppResult.Failure -> _uiState.value.hasUnreadNotifications
                }

                _uiState.value = DashboardUiState(
                    data = dashboardSnapshot.value,
                    isLoading = false,
                    hasUnreadNotifications = hasUnread,
                    freshness = dashboardSnapshot.freshness,
                )
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                val appError = error.toAppError()
                _uiState.update { it.copy(isLoading = false, error = appError) }
                _effects.tryEmit(DashboardUiEffect.ShowError(appError))
            }
        }
        refreshJob = job
        job.invokeOnCompletion { if (refreshJob === job) refreshJob = null }
    }
}

sealed interface DashboardUiAction : UiAction {
    data object Refresh : DashboardUiAction
}

sealed interface DashboardUiEffect : UiEffect {
    data class ShowError(val error: com.wealthvault.core.architecture.AppError) : DashboardUiEffect
}
