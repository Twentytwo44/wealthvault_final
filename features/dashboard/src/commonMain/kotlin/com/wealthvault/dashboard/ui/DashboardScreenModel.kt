package com.wealthvault.dashboard.ui

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.core.FlowResult
import com.wealthvault.dashboard.data.DashboardRepository
import com.wealthvault.notification.usecase.NotificationUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DashboardScreenModel(
    private val repository: DashboardRepository,
    private val notificationUseCase: NotificationUseCase,
) : ScreenModel {

    private val _uiState = MutableStateFlow(DashboardUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    private var refreshJob: Job? = null

    init {
        refresh()
    }

    /** Refresh is explicit and deduplicated; the screen no longer refetches on every lifecycle event. */
    fun refresh() {
        if (refreshJob?.isActive == true) return

        refreshJob = screenModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val dashboardResult = repository.getDashboardData()
            val dashboardError = dashboardResult.exceptionOrNull()
            if (dashboardError != null) {
                _uiState.update { it.copy(isLoading = false, error = dashboardError) }
                return@launch
            }

            val dashboard = dashboardResult.getOrNull()
            var hasUnread = _uiState.value.hasUnreadNotifications
            notificationUseCase(Unit).collect { result ->
                if (result is FlowResult.Continue) {
                    hasUnread = result.data.any { notification -> notification.isRead != true }
                }
            }

            _uiState.value = DashboardUiState(
                data = dashboard,
                isLoading = false,
                hasUnreadNotifications = hasUnread,
            )
        }
    }
}
