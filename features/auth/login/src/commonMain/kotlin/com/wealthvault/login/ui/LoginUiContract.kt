package com.wealthvault.login.ui

import com.wealthvault.core.architecture.AppError
import com.wealthvault.core.architecture.UiAction
import com.wealthvault.core.architecture.UiEffect

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

sealed interface LoginUiAction : UiAction {
    data class UsernameChanged(val value: String) : LoginUiAction
    data class PasswordChanged(val value: String) : LoginUiAction
    data class ValidationFailed(val message: String) : LoginUiAction
    data object Submit : LoginUiAction
}

sealed interface LoginUiEffect : UiEffect {
    data class Navigate(val destination: LoginState) : LoginUiEffect
    data class ShowError(val error: AppError) : LoginUiEffect
}
