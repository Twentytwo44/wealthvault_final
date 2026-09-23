package com.wealthvault.register.ui

import com.wealthvault.core.architecture.UiAction
import com.wealthvault.core.architecture.UiEffect

data class RegisterUiState(
    val username: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

sealed interface RegisterUiAction : UiAction {
    data class UsernameChanged(val value: String) : RegisterUiAction
    data class PasswordChanged(val value: String) : RegisterUiAction
    data class ConfirmPasswordChanged(val value: String) : RegisterUiAction
    data object Submit : RegisterUiAction
}

sealed interface RegisterUiEffect : UiEffect {
    data object Registered : RegisterUiEffect
}
