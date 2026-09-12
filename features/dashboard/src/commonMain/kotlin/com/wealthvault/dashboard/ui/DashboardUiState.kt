package com.wealthvault.dashboard.ui

import com.wealthvault.`user-api`.model.DashboardDataResponse

data class DashboardUiState(
    val data: DashboardDataResponse? = null,
    val isLoading: Boolean = false,
    val hasUnreadNotifications: Boolean = false,
    val error: Throwable? = null,
)
