package com.wealthvault.forgetpassword.ui

import com.wealthvault.core.architecture.AppError
import com.wealthvault.core.architecture.UiAction
import com.wealthvault.core.architecture.UiEffect

data class ForgetPasswordUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val resetToken: String = "",
    val isOtpSent: Boolean = false,
    val isOtpVerified: Boolean = false,
    val isPasswordReset: Boolean = false,
)

sealed interface ForgetPasswordUiAction : UiAction {
    data class SendOtp(val email: String) : ForgetPasswordUiAction
    data class VerifyOtp(val email: String, val otp: String) : ForgetPasswordUiAction
    data class ResetPassword(val token: String, val password: String, val confirm: String) : ForgetPasswordUiAction
    data object Clear : ForgetPasswordUiAction
}

sealed interface ForgetPasswordUiEffect : UiEffect {
    data object PasswordReset : ForgetPasswordUiEffect
    data class ShowError(val error: AppError) : ForgetPasswordUiEffect
}
