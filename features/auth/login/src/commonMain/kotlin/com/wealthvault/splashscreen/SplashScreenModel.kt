package com.wealthvault.splashscreen

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.core.architecture.AppError
import com.wealthvault.core.architecture.UiAction
import com.wealthvault.core.architecture.UiEffect
import com.wealthvault.core.architecture.UiState
import com.wealthvault.core.observability.AppLogger
import com.wealthvault.core.observability.NoOpAppLogger
import com.wealthvault.domain.auth.SessionManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

sealed class SplashState {
    object Loading : SplashState()
    object GoToLogin : SplashState()
    object GoToIntro : SplashState()
    object GoToMain : SplashState()
}

sealed interface SplashUiAction : UiAction {
    data object CheckAuthentication : SplashUiAction
}

sealed interface SplashUiEffect : UiEffect {
    data class RouteTo(val state: SplashState) : SplashUiEffect
    data class ShowError(val error: AppError) : SplashUiEffect
}

class SplashScreenModel(
    private val sessionManager: SessionManager,
    private val logger: AppLogger = NoOpAppLogger,
) : StateScreenModel<SplashState>(SplashState.Loading) {

    private val _uiState = MutableStateFlow<UiState<SplashState>>(
        UiState(data = SplashState.Loading, isLoading = true),
    )
    val uiState: StateFlow<UiState<SplashState>> = _uiState.asStateFlow()
    private val _effects = MutableSharedFlow<SplashUiEffect>(extraBufferCapacity = 1)
    val effects = _effects.asSharedFlow()
    private var checkJob: Job? = null

    fun onAction(action: SplashUiAction) {
        when (action) {
            SplashUiAction.CheckAuthentication -> checkAuthentication()
        }
    }

    // 🌟 ย้ายจาก init มาสร้างฟังก์ชันให้เรียกจากภายนอกได้
    fun checkAuthentication() {
        if (checkJob?.isActive == true) return
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        val job = screenModelScope.launch {
            try {
                // Keep the legacy model aligned with AppCoordinator while it
                // remains available to older launchers.
                delay(350)

                // 🌟 ใช้ firstOrNull เพื่อความปลอดภัย ไม่ให้ Flow ค้าง
                val token = sessionManager.accessToken.firstOrNull()

                if (token.isNullOrBlank()) {
                    publish(SplashState.GoToLogin)
                } else {
                    // 💡 ตรงนี้ในอนาคตถ้าจะเช็กโปรไฟล์/วันเกิด ให้ยิง API ต่อที่นี่ได้เลย
                    publish(SplashState.GoToMain)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Throwable) {
                logger.warn("Splash authentication check failed", e)
                // ถ้า Error ให้ส่งไป Login เพื่อความปลอดภัย
                publish(SplashState.GoToLogin)
                _effects.tryEmit(SplashUiEffect.ShowError(AppError.Unknown(e)))
            }
        }
        checkJob = job
        job.invokeOnCompletion { if (checkJob === job) checkJob = null }
    }

    private fun publish(state: SplashState) {
        mutableState.value = state
        _uiState.value = UiState(data = state, isLoading = state == SplashState.Loading)
        if (state != SplashState.Loading) _effects.tryEmit(SplashUiEffect.RouteTo(state))
    }
}
