package com.wealthvault.introduction.ui

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.domain.profile.UpdateUserDataRequest
import com.wealthvault.domain.profile.ProfileRepository
import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.architecture.AppError
import com.wealthvault.core.architecture.UiState
import com.wealthvault.core.architecture.toThrowable
import com.wealthvault.core.observability.AppLogger
import com.wealthvault.core.observability.NoOpAppLogger
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlin.coroutines.cancellation.CancellationException

class IntroScreenModel(
    private val repository: ProfileRepository,
    private val logger: AppLogger = NoOpAppLogger,
) : ScreenModel {

    private val _uiState = MutableStateFlow(IntroUiState())
    val uiState = _uiState.asStateFlow()

    /** Standard state envelope retained alongside the legacy form projection. */
    val appUiState: StateFlow<UiState<IntroUiState>> = uiState
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
            UiState(data = IntroUiState()),
        )

    private val _effects = MutableSharedFlow<IntroUiEffect>(extraBufferCapacity = 1)
    val effects = _effects.asSharedFlow()

    private var updateJob: Job? = null

    // Compatibility accessors for the existing form while callers migrate to
    // uiState/onAction. They are backed by one immutable StateFlow, not a
    // second Compose state store.
    var userName: String
        get() = _uiState.value.userName
        set(value) { _uiState.update { it.copy(userName = value, errorMessage = null) } }
    var firstName: String
        get() = _uiState.value.firstName
        set(value) { _uiState.update { it.copy(firstName = value, errorMessage = null) } }
    var lastName: String
        get() = _uiState.value.lastName
        set(value) { _uiState.update { it.copy(lastName = value, errorMessage = null) } }
    var phoneNum: String
        get() = _uiState.value.phoneNum
        set(value) { _uiState.update { it.copy(phoneNum = value, errorMessage = null) } }
    var birthday: String
        get() = _uiState.value.birthday
        set(value) { _uiState.update { it.copy(birthday = value, errorMessage = null) } }
    var picture: ByteArray?
        get() = _uiState.value.picture
        set(value) { _uiState.update { it.copy(picture = value) } }
    var isLoading: Boolean
        get() = _uiState.value.isLoading
        private set(value) { _uiState.update { it.copy(isLoading = value) } }
    var errorMessage: String?
        get() = _uiState.value.errorMessage
        private set(value) { _uiState.update { it.copy(errorMessage = value) } }

    fun clearError() {
        errorMessage = null
    }

    fun onAction(action: IntroUiAction, onSuccess: () -> Unit = {}) {
        when (action) {
            is IntroUiAction.UserNameChanged -> userName = action.value
            is IntroUiAction.FirstNameChanged -> firstName = action.value
            is IntroUiAction.LastNameChanged -> lastName = action.value
            is IntroUiAction.PhoneChanged -> phoneNum = action.value
            is IntroUiAction.BirthdayChanged -> birthday = action.value
            is IntroUiAction.PictureChanged -> picture = action.value
            IntroUiAction.Submit -> updateProfile(onSuccess)
        }
    }

    // ฟังก์ชันอัปเดตข้อมูล
    fun updateProfile(onSuccess: () -> Unit) {
        val state = _uiState.value
        validateIntroInput(
            userName = state.userName,
            firstName = state.firstName,
            lastName = state.lastName,
            phoneNum = state.phoneNum,
            birthday = state.birthday,
        )?.let { validationError ->
            errorMessage = validationError
            return
        }

        if (state.isLoading || updateJob?.isActive == true) return
        isLoading = true
        errorMessage = null
        updateJob = screenModelScope.launch {
            try {

                val request = UpdateUserDataRequest(
                    firstName = state.firstName,
                    lastName = state.lastName,
                    phoneNumber = state.phoneNum,
                    profileImage = state.picture,
                    birthday = state.birthday,
                    username = state.userName,
                    sharedEnabled = null,
                    sharedAge = null
                )

                when (val result = repository.updateUserData(request)) {
                    is AppResult.Success -> {
                        logger.info("Profile update succeeded")
                        _effects.tryEmit(IntroUiEffect.Updated)
                        onSuccess()
                    }
                    is AppResult.Failure -> {
                        val error = result.error.toThrowable()
                        errorMessage = error.message ?: "เกิดข้อผิดพลาดในการอัปเดตข้อมูล"
                        _effects.tryEmit(IntroUiEffect.ShowError(result.error))
                        logger.warn("Profile update failed", error)
                    }
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                errorMessage = error.message ?: "เกิดข้อผิดพลาดในการอัปเดตข้อมูล"
                logger.warn("Profile update failed unexpectedly", error)
            } finally {
                isLoading = false
                updateJob = null
            }
        }
    }
}
