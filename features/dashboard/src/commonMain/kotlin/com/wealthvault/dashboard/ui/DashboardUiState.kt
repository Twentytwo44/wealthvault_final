package com.wealthvault.dashboard.ui

import com.wealthvault.core.architecture.AppError
import com.wealthvault.core.architecture.CacheFreshness
import com.wealthvault.core.model.DashboardData

data class DashboardUiState(
    val data: DashboardData? = null,
    val isLoading: Boolean = false,
    val hasUnreadNotifications: Boolean = false,
    val error: AppError? = null,
    val freshness: CacheFreshness = CacheFreshness.Fresh,
)
