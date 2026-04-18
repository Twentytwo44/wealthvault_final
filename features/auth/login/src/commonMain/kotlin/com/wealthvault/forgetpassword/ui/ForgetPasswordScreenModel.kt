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

    // --- State ต่างๆ ---
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // 🌟 ใช้เก็บ Token ชั่วคราวหลังจากยืนยัน OTP เสร็จ เพื่อเอาไปใช้ตอน Reset
    private val _resetToken = MutableStateFlow<String>("")
    val resetToken: StateFlow<String> = _resetToken.asStateFlow()

    // State สำหรับบอกว่าสำเร็จเพื่อเปลี่ยนหน้า
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
                    is FlowResult.Failure -> _errorMessage.value = result.cause?.message ?: "ส่ง OTP ไม่สำเร็จ"
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
                        // 🌟 ดึงค่า Token ของจริงที่ส่งมาจาก OTPUseCase
                        // (ลองพิมพ์ result. แล้วดูว่ามันชื่อ data, value หรืออะไร แล้วใส่ให้ตรงครับ)
                        _resetToken.value = result.data ?: "" // หรืออาจจะเป็น result.value

                        _isOtpVerified.value = true
                    }
                    is FlowResult.Failure -> _errorMessage.value = result.cause?.message ?: "OTP ไม่ถูกต้อง"
                    else -> {}
                }
            }
            _isLoading.value = false
        }
    }

    fun resetPassword(token: String, password: String, confirm: String) {
        if (password != confirm) {
            _errorMessage.value = "รหัสผ่านไม่ตรงกัน"
            return
        }

        screenModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            // 🌟 นำ token ที่รับมาส่งเข้าไปใน Request
            resetPasswordUseCase(ResetPasswordRequest(resettoken = token, password = password)).collect { result ->
                when (result) {
                    is FlowResult.Continue -> _isPasswordReset.value = true
                    is FlowResult.Failure -> _errorMessage.value = result.cause?.message ?: "เปลี่ยนรหัสผ่านไม่สำเร็จ" // (ใช้ error, throwable หรือ cause ตามที่คุณ Champ แก้ไว้นะครับ)
                    else -> {}
                }
            }
            _isLoading.value = false
        }
    }

    // 🌟 เพิ่มฟังก์ชันสำหรับล้างค่า State ตอนเปลี่ยนหน้า
    fun clearFlags() {
        _isOtpSent.value = false
        _isOtpVerified.value = false
        _isPasswordReset.value = false
    }
}