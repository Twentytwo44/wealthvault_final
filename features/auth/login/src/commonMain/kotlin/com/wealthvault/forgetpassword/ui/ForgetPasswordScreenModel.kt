package com.wealthvault.forgetpassword.ui

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.domain.auth.OtpVerificationRequest
import com.wealthvault.domain.auth.PasswordRecoveryRequest
import com.wealthvault.domain.auth.PasswordResetRequest
import com.wealthvault.core.architecture.AppError
import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.architecture.UiState
import com.wealthvault.core.architecture.toThrowable
import com.wealthvault.forgetpassword.usecase.ForgetUsecase
import com.wealthvault.forgetpassword.usecase.OTPUseCase
import com.wealthvault.forgetpassword.usecase.ResetPasswordUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

class ForgetPasswordScreenModel(
    private val forgetUsecase: ForgetUsecase,
    private val otpUseCase: OTPUseCase,
    private val resetPasswordUseCase: ResetPasswordUseCase
) : ScreenModel {

    private fun setError(message: String) {
        _errorMessage.value = message
        _effects.tryEmit(
            ForgetPasswordUiEffect.ShowError(AppError.Unknown(IllegalStateException(message))),
        )
    }

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _resetToken = MutableStateFlow<String>("")
    val resetToken: StateFlow<String> = _resetToken.asStateFlow()

    private val _isOtpSent = MutableStateFlow(false)
    val isOtpSent: StateFlow<Boolean> = _isOtpSent.asStateFlow()

    private val _isOtpVerified = MutableStateFlow(false)
    val isOtpVerified: StateFlow<Boolean> = _isOtpVerified.asStateFlow()

    private val _isPasswordReset = MutableStateFlow(false)
    val isPasswordReset: StateFlow<Boolean> = _isPasswordReset.asStateFlow()

    private val _effects = MutableSharedFlow<ForgetPasswordUiEffect>(extraBufferCapacity = 1)
    val effects = _effects.asSharedFlow()

    /** Recovery is a single-step-at-a-time flow; do not enqueue duplicate OTP
     * or reset requests while the previous action is still pending. */
    private fun runExclusive(action: suspend () -> Unit) {
        if (_isLoading.value) return
        _isLoading.value = true
        screenModelScope.launch {
            try {
                action()
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                setError(error.message ?: "ดำเนินการไม่สำเร็จ กรุณาลองใหม่")
            } finally {
                _isLoading.value = false
            }
        }
    }

    private val recoveryState = combine(
        _isLoading,
        _errorMessage,
        _resetToken,
    ) { loading, error, token -> Triple(loading, error, token) }

    private val verificationState = combine(
        _isOtpSent,
        _isOtpVerified,
        _isPasswordReset,
    ) { otpSent, otpVerified, passwordReset -> Triple(otpSent, otpVerified, passwordReset) }

    val uiState: StateFlow<ForgetPasswordUiState> = combine(
        recoveryState,
        verificationState,
    ) { recovery, verification ->
        ForgetPasswordUiState(
            isLoading = recovery.first,
            errorMessage = recovery.second,
            resetToken = recovery.third,
            isOtpSent = verification.first,
            isOtpVerified = verification.second,
            isPasswordReset = verification.third,
        )
    }.stateIn(screenModelScope, SharingStarted.Eagerly, ForgetPasswordUiState())

    /** Shared application state contract; [uiState] remains for old routes. */
    val appUiState: StateFlow<UiState<ForgetPasswordUiState>> = uiState
        .map { state ->
            UiState(
                data = state,
                isLoading = state.isLoading,
                error = state.errorMessage?.let { AppError.Unknown(IllegalStateException(it)) },
            )
        }
        .stateIn(screenModelScope, SharingStarted.Eagerly, UiState(data = ForgetPasswordUiState()))

    fun onAction(action: ForgetPasswordUiAction) {
        when (action) {
            is ForgetPasswordUiAction.SendOtp -> sendOtp(action.email)
            is ForgetPasswordUiAction.VerifyOtp -> verifyOtp(action.email, action.otp)
            is ForgetPasswordUiAction.ResetPassword -> resetPassword(action.token, action.password, action.confirm)
            ForgetPasswordUiAction.Clear -> clearFlags()
        }
    }

    fun resetState() {
        _errorMessage.value = null
    }

    // --- 1. ยิง API ขอ OTP ---
    fun sendOtp(email: String) {
        if (email.isBlank()) {
            setError("กรุณากรอกอีเมล")
            return
        }

        runExclusive {
            _errorMessage.value = null

            when (val result = forgetUsecase.requestOtp(PasswordRecoveryRequest(email))) {
                is AppResult.Success -> _isOtpSent.value = true
                is AppResult.Failure -> {
                    val rawError = result.error.toThrowable().message ?: "ส่ง OTP ไม่สำเร็จ"
                    if (rawError.contains("user not found", ignoreCase = true)) {
                        setError("ไม่พบบัญชีผู้ใช้นี้ในระบบ")
                    } else {
                        setError(rawError)
                    }
                }
            }
        }
    }

    // --- 2. ยิง API ยืนยัน OTP ---
    fun verifyOtp(email: String, otp: String) {
        if (otp.length != 6) {
            setError("กรุณากรอก OTP ให้ครบ 6 หลัก")
            return
        }

        runExclusive {
            _errorMessage.value = null

            when (val result = otpUseCase.verify(OtpVerificationRequest(email, otp))) {
                is AppResult.Success -> {
                    _resetToken.value = result.value
                    _isOtpVerified.value = true
                }
                is AppResult.Failure -> {
                    val rawError = result.error.toThrowable().message ?: "OTP ไม่ถูกต้อง"
                    // 🌟 ดักจับ Error จาก Backend: OTP ผิด หรือ หมดอายุ
                    if (rawError.contains("invalid or expired OTP", ignoreCase = true)) {
                        setError("รหัส OTP ไม่ถูกต้องหรือหมดอายุ")
                    } else {
                        setError(rawError)
                    }
                }
            }
        }
    }

    // --- 3. ยิง API เปลี่ยนรหัสผ่านใหม่ ---
    // --- 3. ยิง API เปลี่ยนรหัสผ่านใหม่ ---
    fun resetPassword(token: String, password: String, confirm: String) {
        if (password != confirm) {
            setError("รหัสผ่านไม่ตรงกัน")
            return
        }

        if (password.isBlank()) {
            setError("กรุณากรอกรหัสผ่านใหม่")
            return
        }

        runExclusive {
            _errorMessage.value = null

            when (val result = resetPasswordUseCase.resetPassword(PasswordResetRequest(resetToken = token, password = password))) {
                is AppResult.Success -> {
                    _isPasswordReset.value = true
                    _effects.tryEmit(ForgetPasswordUiEffect.PasswordReset)
                }
                is AppResult.Failure -> {
                    val rawError = result.error.toThrowable().message ?: "เปลี่ยนรหัสผ่านไม่สำเร็จ"

                    // 🌟 ดักจับ Error ถ้ารหัสผ่านใหม่ไปซ้ำกับรหัสผ่านเดิม
                    if (rawError.contains("new password cannot be the same as the old password", ignoreCase = true)) {
                        setError("รหัสผ่านใหม่ต้องไม่ซ้ำกับรหัสผ่านเดิม")
                    } else {
                        setError(rawError)
                    }
                }
            }
        }
    }

    fun clearFlags() {
        _isOtpSent.value = false
        _isOtpVerified.value = false
        _isPasswordReset.value = false
    }
}
