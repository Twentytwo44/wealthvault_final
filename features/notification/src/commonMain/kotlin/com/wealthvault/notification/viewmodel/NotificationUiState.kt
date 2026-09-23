package com.wealthvault.notification.viewmodel

import com.wealthvault.core.architecture.AppError
import com.wealthvault.core.architecture.CacheFreshness
import com.wealthvault.core.architecture.UiAction
import com.wealthvault.core.architecture.UiEffect
import com.wealthvault.core.model.NotificationItem

data class NotificationUiState(
    val items: List<NotificationItem> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val freshness: CacheFreshness = CacheFreshness.Fresh,
    val error: AppError? = null,
)

sealed interface NotificationUiAction : UiAction {
    data object Refresh : NotificationUiAction
}

sealed interface NotificationUiEffect : UiEffect {
    data class ShowError(val error: AppError) : NotificationUiEffect
}
