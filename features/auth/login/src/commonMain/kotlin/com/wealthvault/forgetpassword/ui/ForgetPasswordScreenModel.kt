package com.wealthvault.forgetpassword.ui

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.`auth-api`.model.ForgetPasswordRequest
import com.wealthvault.`auth-api`.model.OTPRequest
import com.wealthvault.`auth-api`.model.ResetPasswordRequest
import com.wealthvault.core.FlowResult
import com.wealthvault.forgetpassword.usecase.ForgetUsecase
import com.wealthvault.forgetpassword.usecase.OTPUseCase
import com.wealthvault.forgetpassword.usecase.ResetPasswordUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ForgetPasswordScreenModel(
    private val forgetUsecase: ForgetUsecase,
    private val otpUseCase: OTPUseCase,
    private val resetPasswordUseCase: ResetPasswordUseCase
) : ScreenModel {

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

    fun resetState() {
        _errorMessage.value = null
    }

    // --- 1. ยิง API ขอ OTP ---
    fun sendOtp(email: String) {
        if (email.isBlank()) {
            _errorMessage.value = "กรุณากรอกอีเมล"
            return
        }

        screenModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            forgetUsecase(ForgetPasswordRequest(email)).collect { result ->
                when (result) {
                    is FlowResult.Continue -> _isOtpSent.value = true
                    is FlowResult.Failure -> {
                        val rawError = result.cause?.message ?: "ส่ง OTP ไม่สำเร็จ"
                        if (rawError.contains("user not found", ignoreCase = true)) {
                            _errorMessage.value = "ไม่พบบัญชีผู้ใช้นี้ในระบบ"
                        } else {
                            _errorMessage.value = rawError
                        }
                    }
                    else -> {}
                }
            }
            _isLoading.value = false
        }
    }

    // --- 2. ยิง API ยืนยัน OTP ---
    fun verifyOtp(email: String, otp: String) {
        if (otp.length != 6) {
            _errorMessage.value = "กรุณากรอก OTP ให้ครบ 6 หลัก"
            return
        }

        screenModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            otpUseCase(OTPRequest(email, otp)).collect { result ->
                when (result) {
                    is FlowResult.Continue -> {
                        _resetToken.value = result.data ?: ""
                        _isOtpVerified.value = true
                    }
                    is FlowResult.Failure -> {
                        val rawError = result.cause?.message ?: "OTP ไม่ถูกต้อง"
                        // 🌟 ดักจับ Error จาก Backend: OTP ผิด หรือ หมดอายุ
                        if (rawError.contains("invalid or expired OTP", ignoreCase = true)) {
                            _errorMessage.value = "รหัส OTP ไม่ถูกต้องหรือหมดอายุ"
                        } else {
                            _errorMessage.value = rawError
                        }
                    }
                    else -> {}
                }
            }
            _isLoading.value = false
        }
    }

    // --- 3. ยิง API เปลี่ยนรหัสผ่านใหม่ ---
    // --- 3. ยิง API เปลี่ยนรหัสผ่านใหม่ ---
    fun resetPassword(token: String, password: String, confirm: String) {
        if (password != confirm) {
            _errorMessage.value = "รหัสผ่านไม่ตรงกัน"
            return
        }

        if (password.isBlank()) {
            _errorMessage.value = "กรุณากรอกรหัสผ่านใหม่"
            return
        }

        screenModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            resetPasswordUseCase(ResetPasswordRequest(resettoken = token, password = password)).collect { result ->
                when (result) {
                    is FlowResult.Continue -> _isPasswordReset.value = true
                    is FlowResult.Failure -> {
                        val rawError = result.cause?.message ?: "เปลี่ยนรหัสผ่านไม่สำเร็จ"

                        // 🌟 ดักจับ Error ถ้ารหัสผ่านใหม่ไปซ้ำกับรหัสผ่านเดิม
                        if (rawError.contains("new password cannot be the same as the old password", ignoreCase = true)) {
                            _errorMessage.value = "รหัสผ่านใหม่ต้องไม่ซ้ำกับรหัสผ่านเดิม"
                        } else {
                            _errorMessage.value = rawError
                        }
                    }
                    else -> {}
                }
            }
            _isLoading.value = false
        }
    }

    fun clearFlags() {
        _isOtpSent.value = false
        _isOtpVerified.value = false
        _isPasswordReset.value = false
    }
}