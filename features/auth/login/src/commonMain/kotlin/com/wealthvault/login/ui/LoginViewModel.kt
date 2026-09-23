package com.wealthvault.login.ui

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.architecture.AppError
import com.wealthvault.core.architecture.UiState
import com.wealthvault.core.architecture.toThrowable
import com.wealthvault.core.observability.AppLogger
import com.wealthvault.core.observability.NoOpAppLogger
import com.wealthvault.domain.auth.LoginCredentials
import com.wealthvault.domain.auth.ProviderToken
import com.wealthvault.domain.auth.ProviderAuthRepository
import com.wealthvault.domain.auth.GoogleSignInProvider
import com.wealthvault.login.usecase.LoginUseCase
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

sealed class LoginState {
    object Loading : LoginState()
    object GoToIntro : LoginState()
    object GoToMain : LoginState()
}

class LoginScreenModel(
    private val loginUseCase: LoginUseCase,
    private val googleRepository: GoogleSignInProvider,
    private val googleLink: ProviderAuthRepository,
    private val logger: AppLogger = NoOpAppLogger,
) : ScreenModel {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    /**
     * Shared application contract exposed during the compatibility migration.
     * The legacy form state remains available to existing routes, while new
     * callers can consume one standard loading/error envelope.
     */
    val appUiState: StateFlow<UiState<LoginUiState>> = uiState
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
            UiState(data = LoginUiState()),
        )

    private val _effects = MutableSharedFlow<LoginUiEffect>(extraBufferCapacity = 1)
    val effects = _effects.asSharedFlow()

    /** One authentication request at a time, including provider sign-in. */
    private var authJob: Job? = null

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

    @Suppress("UNUSED_PARAMETER")
    fun onAction(action: LoginUiAction, onNavigate: (LoginState) -> Unit = {}) {
        when (action) {
            is LoginUiAction.UsernameChanged -> _uiState.update {
                it.copy(username = action.value, errorMessage = null)
            }
            is LoginUiAction.PasswordChanged -> _uiState.update {
                it.copy(password = action.value, errorMessage = null)
            }
            is LoginUiAction.ValidationFailed -> _uiState.update {
                it.copy(isLoading = false, errorMessage = action.message)
            }
            // Authentication routing is owned by AppCoordinator. Keep the
            // callback parameter for source compatibility with older callers,
            // but do not let a feature-owned callback navigate globally.
            LoginUiAction.Submit -> onLoginClick()
        }
    }

    // 🌟 ฟังก์ชันสำหรับแปลง Error จาก Backend / Network ให้เป็นภาษาไทยที่อ่านง่าย
    private fun parseErrorMessage(rawError: String?): String {
        if (rawError.isNullOrBlank()) return "เกิดข้อผิดพลาดบางอย่าง กรุณาลองใหม่"

        val lowerError = rawError.lowercase()

        return when {
            // เคสพิมพ์ผิด (อิงจาก RPC Error ของหลังบ้าน)
            lowerError.contains("invalid email or password") ||
                    lowerError.contains("wrong password") ||
                    lowerError.contains("user not found") -> "อีเมลหรือรหัสผ่านไม่ถูกต้อง"

            // เคสปัญหาเครือข่าย/เน็ตหลุด/เซิร์ฟเวอร์ตาย
            lowerError.contains("timeout") ||
                    lowerError.contains("failed to connect") ||
                    lowerError.contains("unknownhost") ||
                    lowerError.contains("network") ||
                    lowerError.contains("connection refused") -> "ไม่สามารถเชื่อมต่อเซิร์ฟเวอร์ได้"

            // เคสอื่นๆ ที่ไม่ได้ดักไว้ (ตัดคำว่า rpc error ทิ้งถ้ามี เพื่อให้ดูสะอาดขึ้น)
            else -> "เข้าสู่ระบบไม่สำเร็จ: ${rawError.replace("rpc error: code = Internal desc = ", "")}"
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun onLoginClick(onNavigate: (LoginState) -> Unit) = onLoginClick()

    fun onLoginClick() {
        if (isLoading || authJob?.isActive == true) return
        if (username.isBlank() || password.isBlank()) {
            errorMessage = "กรุณากรอกข้อมูลให้ครบถ้วน"
            return
        }

        // Set this before launching so two taps in the same UI frame cannot
        // enqueue two requests before the coroutine gets scheduled.
        isLoading = true
        errorMessage = null
        authJob = screenModelScope.launch {
            try {
                val request = LoginCredentials(username = username, password = password)

                when (val result = loginUseCase.login(request)) {
                    is AppResult.Success -> {
                        logger.info("Login succeeded; session routing will continue centrally")
                    }
                    is AppResult.Failure -> {
                        _effects.tryEmit(com.wealthvault.login.ui.LoginUiEffect.ShowError(result.error))
                        errorMessage = parseErrorMessage(result.error.toThrowable().message)
                    }
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                logger.warn("Login request failed unexpectedly", error)
                errorMessage = parseErrorMessage(error.message)
            } finally {
                isLoading = false
                authJob = null
            }
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun onGoogleClick(onNavigate: (LoginState) -> Unit = {}) {
        if (isLoading || authJob?.isActive == true) return
        isLoading = true
        errorMessage = null
        authJob = screenModelScope.launch {
            try {
                val signInResult = googleRepository.signIn()
                val user = when (signInResult) {
                    is AppResult.Success -> signInResult.value
                    is AppResult.Failure -> throw signInResult.error.toThrowable()
                }

                logger.debug("Google identity received")

                if (user == null) {
                    errorMessage = "ยกเลิกการเข้าสู่ระบบผ่าน Google"
                    return@launch
                }

                val request = ProviderToken(
                    token = user.idToken
                )

                when (val response = googleLink.login(request)) {
                    is com.wealthvault.core.architecture.AppResult.Success -> {
                        val data = response.value
                        if (data.success == true) {
                            logger.info("Google login succeeded; session routing will continue centrally")
                        } else {
                            errorMessage = "เข้าสู่ระบบด้วย Google ไม่สำเร็จ"
                        }
                    }
                    is com.wealthvault.core.architecture.AppResult.Failure -> {
                        val exception = response.error.toThrowable()
                        logger.warn("Google login failed", exception)
                        errorMessage = parseErrorMessage(exception.message)
                    }
                }

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                logger.warn("Google login request failed", e)
                // 🌟 นำข้อความ Error ไปผ่านตัวกรองก่อนแสดงผล
                errorMessage = parseErrorMessage(e.message)
            } finally {
                isLoading = false
                authJob = null
            }
        }
    }
}
