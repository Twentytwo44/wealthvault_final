package com.wealthvault.profile.ui

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.core.architecture.AppError
import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.architecture.UiAction
import com.wealthvault.core.architecture.UiEffect
import com.wealthvault.core.architecture.UiState
import com.wealthvault.core.architecture.toAppError
import com.wealthvault.core.architecture.toThrowable
import com.wealthvault.core.observability.AppLogger
import com.wealthvault.core.observability.platformLogger
import com.wealthvault.domain.auth.SessionManager
import com.wealthvault.domain.profile.DeviceRegistrationRepository
import com.wealthvault.domain.profile.LineLinkRepository
import com.wealthvault.domain.profile.LineSignInProvider
import com.wealthvault.domain.profile.LineUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

sealed interface MenuProfileUiAction : UiAction {
    data class LineClicked(val auth: LineSignInProvider) : MenuProfileUiAction
    data class LineSucceeded(val user: LineUser, val onSuccess: () -> Unit) : MenuProfileUiAction
    data class LineFailed(val message: String) : MenuProfileUiAction
    data class UnregisterDevice(val onComplete: () -> Unit) : MenuProfileUiAction
}

sealed interface MenuProfileUiEffect : UiEffect {
    data object Linked : MenuProfileUiEffect
    data object LoggedOut : MenuProfileUiEffect
    data class ShowError(val error: AppError) : MenuProfileUiEffect
}

class MenuProfileSettingScreenModel(
    private val deviceRegistrationRepository: DeviceRegistrationRepository,
    private val sessionManager: SessionManager,
    private val lineLinkRepository: LineLinkRepository,
    private val logger: AppLogger = platformLogger(),
) : ScreenModel {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _uiState = MutableStateFlow(UiState<Unit>(data = Unit))
    val uiState: StateFlow<UiState<Unit>> = _uiState.asStateFlow()
    private val _effects = MutableSharedFlow<MenuProfileUiEffect>(extraBufferCapacity = 1)
    val effects = _effects.asSharedFlow()
    private var lineLinkJob: Job? = null
    private var unregisterJob: Job? = null
    private val logoutCoordinator = SessionLogoutCoordinator(
        deviceRepository = deviceRegistrationRepository,
        sessionManager = sessionManager,
        logger = logger,
    )

    fun onAction(action: MenuProfileUiAction) {
        when (action) {
            is MenuProfileUiAction.LineClicked -> onLineClick(action.auth)
            is MenuProfileUiAction.LineSucceeded -> onLineSuccess(action.user, action.onSuccess)
            is MenuProfileUiAction.LineFailed -> onLineError(action.message)
            is MenuProfileUiAction.UnregisterDevice -> unRegisterDevice(action.onComplete)
        }
    }

    private fun setLoading(value: Boolean) {
        _isLoading.value = value
        _uiState.value = _uiState.value.copy(data = Unit, isLoading = value)
    }

    fun onLineClick(lineAuth: LineSignInProvider) {
        if (_isLoading.value || lineLinkJob?.isActive == true) return
        setLoading(true)
        logger.debug("LINE login started")
        try {
            lineAuth.login()
        } catch (error: Throwable) {
            setLoading(false)
            val appError = error.toAppError()
            logger.warn("LINE login failed", error)
            _uiState.value = _uiState.value.copy(error = appError)
            _effects.tryEmit(MenuProfileUiEffect.ShowError(appError))
        }
    }

    fun onLineSuccess(user: LineUser, onSuccess: () -> Unit) {
        if (lineLinkJob?.isActive == true) return
        logger.info("LINE login succeeded")
        if (!_isLoading.value) setLoading(true)

        val job = screenModelScope.launch {
            try {
                // 🌟 1. ใส่ Time-out 10 วินาที ถ้าเซิร์ฟเวอร์ค้าง แอปจะได้ไม่ค้างตาม
                val response = withTimeoutOrNull(10000) {
                    lineLinkRepository.link(user.idToken.orEmpty())
                }

                when (response) {
                    is AppResult.Success -> {
                        logger.info("Link LINE account succeeded")
                        onSuccess()
                        _effects.tryEmit(MenuProfileUiEffect.Linked)
                    }

                    is AppResult.Failure -> {
                        logger.warn("Link LINE account failed", response.error.toThrowable())
                        _uiState.value = _uiState.value.copy(error = response.error)
                        _effects.tryEmit(MenuProfileUiEffect.ShowError(response.error))
                    }

                    null -> {
                        val error = AppError.Network(IllegalStateException("การเชื่อมต่อใช้เวลานานเกินไป (Timeout)"))
                        logger.warn("Link LINE account failed", error.toThrowable())
                        _uiState.value = _uiState.value.copy(error = error)
                        _effects.tryEmit(MenuProfileUiEffect.ShowError(error))
                    }
                }

            } catch (e: CancellationException) {
                throw e
            } catch (e: Throwable) {
                logger.warn("Link LINE account request failed", e)
                val error = e.toAppError()
                _uiState.value = _uiState.value.copy(error = error)
                _effects.tryEmit(MenuProfileUiEffect.ShowError(error))

            } finally {
                setLoading(false)
            }
        }
        lineLinkJob = job
        job.invokeOnCompletion { if (lineLinkJob === job) lineLinkJob = null }
    }

    fun onLineError(error: String) {
        setLoading(false)
        logger.warn("LINE login failed", IllegalStateException(error))
        val appError = AppError.Unknown(IllegalStateException(error))
        _uiState.value = _uiState.value.copy(error = appError)
        _effects.tryEmit(MenuProfileUiEffect.ShowError(appError))
    }

    fun unRegisterDevice(onLogoutComplete: () -> Unit) {
        if (_isLoading.value || unregisterJob?.isActive == true) return
        setLoading(true)
        val job = screenModelScope.launch {
            try {
                when (val result = logoutCoordinator.logout()) {
                    is AppResult.Success -> {
                        _effects.tryEmit(MenuProfileUiEffect.LoggedOut)
                        onLogoutComplete()
                    }
                    is AppResult.Failure -> {
                        _uiState.value = _uiState.value.copy(error = result.error)
                        _effects.tryEmit(MenuProfileUiEffect.ShowError(result.error))
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Throwable) {
                logger.warn("Logout failed unexpectedly", e)
                val error = e.toAppError()
                _uiState.value = _uiState.value.copy(error = error)
                _effects.tryEmit(MenuProfileUiEffect.ShowError(error))
            } finally {
                setLoading(false)
            }
        }
        unregisterJob = job
        job.invokeOnCompletion { if (unregisterJob === job) unregisterJob = null }
    }
}
