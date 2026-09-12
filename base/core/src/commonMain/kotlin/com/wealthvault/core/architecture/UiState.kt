package com.wealthvault.core.architecture

data class UiState<out T>(
    val data: T? = null,
    val isLoading: Boolean = false,
    val error: AppError? = null,
)
