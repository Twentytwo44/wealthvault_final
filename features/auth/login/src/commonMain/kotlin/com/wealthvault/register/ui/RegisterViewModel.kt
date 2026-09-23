package com.wealthvault.register.ui

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.architecture.AppError
import com.wealthvault.core.architecture.UiState
import com.wealthvault.core.architecture.toThrowable
import com.wealthvault.core.observability.AppLogger
import com.wealthvault.core.observability.NoOpAppLogger
import com.wealthvault.domain.auth.RegistrationCredentials
import com.wealthvault.register.usecase.RegisterUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

class RegisterScreenModel(
    private val registerUseCase: RegisterUseCase,
    private val logger: AppLogger = NoOpAppLogger,
) : ScreenModel {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState = _uiState.asStateFlow()

    /** Standard state envelope retained alongside the legacy form projection. */
    val appUiState: StateFlow<UiState<RegisterUiState>> = uiState
        .map { state ->
            UiState(
                data = state,
                isLoading = state.isLoading,
                error = state.errorMessage?.let { AppError.Unknown(IllegalStateException(it)) },
            )
        }
        .stateIn(
            screenModelScope,
            SharingStarted.Eagerly,
            UiState(data = RegisterUiState()),
        )

    private val _effects = MutableSharedFlow<RegisterUiEffect>(extraBufferCapacity = 1)
    val effects = _effects.asSharedFlow()

    var username: String
        get() = _uiState.value.username
        set(value) {
            _uiState.update { it.copy(username = value) }
        }
    var password: String
        get() = _uiState.value.password
        set(value) {
            _uiState.update { it.copy(password = value) }
        }
    var confirmPassword: String
        get() = _uiState.value.confirmPassword
        set(value) {
            _uiState.update { it.copy(confirmPassword = value) }
        }

    var isLoading: Boolean
        get() = _uiState.value.isLoading
        set(value) {
            _uiState.update { it.copy(isLoading = value) }
        }
    var errorMessage: String?
        get() = _uiState.value.errorMessage
        set(value) {
            _uiState.update { it.copy(errorMessage = value) }
        }

    fun onAction(action: RegisterUiAction, onSuccess: () -> Unit = {}) {
        when (action) {
            is RegisterUiAction.UsernameChanged -> _uiState.update {
                it.copy(username = action.value, errorMessage = null)
            }
            is RegisterUiAction.PasswordChanged -> _uiState.update {
                it.copy(password = action.value, errorMessage = null)
            }
            is RegisterUiAction.ConfirmPasswordChanged -> _uiState.update {
                it.copy(confirmPassword = action.value, errorMessage = null)
            }
            RegisterUiAction.Submit -> onRegisterClick(onSuccess)
        }
    }

    fun onRegisterClick(onSuccess: () -> Unit) {
        if (isLoading) return

        validateRegistrationInput(username, password, confirmPassword)?.let { validationError ->
            errorMessage = validationError
            return
        }

        // เริ่มโหลดและล้าง Error เก่าทิ้ง
        isLoading = true
        errorMessage = null

        screenModelScope.launch {
            try {
                val request = RegistrationCredentials(
                    username = username,
                    password = password,
                )

                // AppResult is the single result contract for new presentation code.
                when (val result = registerUseCase.register(request)) {
                    is AppResult.Success -> {
                        logger.info("Registration completed")
                        _effects.tryEmit(RegisterUiEffect.Registered)
                        onSuccess()
                    }
                    is AppResult.Failure -> {
                        logger.warn("Registration failed", result.error.toThrowable())
                        errorMessage = result.error.toThrowable().message
                            ?: "การสมัครสมาชิกล้มเหลว กรุณาลองใหม่"
                    }
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                logger.warn("Registration failed unexpectedly", error)
                errorMessage = error.message ?: "การสมัครสมาชิกล้มเหลว กรุณาลองใหม่"
            } finally {
                isLoading = false
            }
        }
    }

    // ฟังก์ชันจำลองเมื่อกดสมัครด้วย Google (เผื่อทำต่อ)
    fun onGoogleClick(onSuccess: () -> Unit) {
        if (isLoading) return

        isLoading = true
        screenModelScope.launch {
            errorMessage = null
            try {
                // This callback is kept for the existing route contract. The
                // registration screen currently has no provider binding, so it
                // must not pretend that a Google account was created.
                errorMessage = "การสมัครด้วย Google ยังไม่พร้อมใช้งาน"
            } finally {
                isLoading = false
            }
        }
    }
}
