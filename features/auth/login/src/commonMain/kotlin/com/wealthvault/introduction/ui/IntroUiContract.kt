package com.wealthvault.introduction.ui

import com.wealthvault.core.architecture.AppError
import com.wealthvault.core.architecture.UiAction
import com.wealthvault.core.architecture.UiEffect

/** Immutable onboarding form state shared by Android and iOS. */
data class IntroUiState(
    val userName: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val phoneNum: String = "",
    val birthday: String = "",
    val picture: ByteArray? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

sealed interface IntroUiAction : UiAction {
    data class UserNameChanged(val value: String) : IntroUiAction
    data class FirstNameChanged(val value: String) : IntroUiAction
    data class LastNameChanged(val value: String) : IntroUiAction
    data class PhoneChanged(val value: String) : IntroUiAction
    data class BirthdayChanged(val value: String) : IntroUiAction
    data class PictureChanged(val value: ByteArray?) : IntroUiAction
    data object Submit : IntroUiAction
}

sealed interface IntroUiEffect : UiEffect {
    data object Updated : IntroUiEffect
    data class ShowError(val error: AppError) : IntroUiEffect
}
