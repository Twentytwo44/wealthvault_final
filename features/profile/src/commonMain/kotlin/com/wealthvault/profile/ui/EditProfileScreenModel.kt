package com.wealthvault.profile.ui

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.core.architecture.AppError
import com.wealthvault.core.architecture.UiAction
import com.wealthvault.core.architecture.UiEffect
import com.wealthvault.core.architecture.UiState
import com.wealthvault.core.architecture.onFailure
import com.wealthvault.core.architecture.onSuccess
import com.wealthvault.core.observability.AppLogger
import com.wealthvault.core.observability.platformLogger
import com.wealthvault.domain.profile.ProfileRepository
import com.wealthvault.domain.profile.UpdateUserDataRequest
import com.wealthvault.domain.profile.UserData
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job

data class EditProfileUiData(
    val user: UserData? = null,
    val selectedImage: ByteArray? = null,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
)

sealed interface EditProfileUiAction : UiAction {
    data object Fetch : EditProfileUiAction
    data class ImageChanged(val value: ByteArray?) : EditProfileUiAction
    data class Save(
        val username: String,
        val firstName: String,
        val lastName: String,
        val birthDate: String,
        val phone: String,
    ) : EditProfileUiAction
    data object ResetSaveState : EditProfileUiAction
}

sealed interface EditProfileUiEffect : UiEffect {
    data object Saved : EditProfileUiEffect
    data class ShowError(val error: AppError) : EditProfileUiEffect
}

class EditProfileScreenModel(
    private val repository: ProfileRepository,
    private val logger: AppLogger = platformLogger(),
) : ScreenModel {
    private val _profileImageByteArray = MutableStateFlow<ByteArray?>(null)
    val profileImageByteArray = _profileImageByteArray.asStateFlow()

    fun setProfileImageByteArray(data: ByteArray?) {
        _profileImageByteArray.value = data
        _uiState.update { state ->
            state.copy(data = (state.data ?: EditProfileUiData()).copy(selectedImage = data))
        }
    }

    // ข้อมูล User ล่าสุด
    private val _userState = MutableStateFlow<UserData?>(null)
    val userState = _userState.asStateFlow()

    // 🌟 1. เพิ่ม State สำหรับบอกว่ากำลังดึงข้อมูล User อยู่หรือไม่ (แก้โหลดค้าง)
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    // สถานะการบันทึก
    private val _isSaving = MutableStateFlow(false)
    val isSaving = _isSaving.asStateFlow()

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess = _saveSuccess.asStateFlow()

    private val _uiState = MutableStateFlow(UiState(data = EditProfileUiData()))
    val uiState: StateFlow<UiState<EditProfileUiData>> = _uiState.asStateFlow()
    private val _effects = MutableSharedFlow<EditProfileUiEffect>(extraBufferCapacity = 1)
    val effects = _effects.asSharedFlow()

    private var fetchJob: Job? = null
    private var saveJob: Job? = null

    fun onAction(action: EditProfileUiAction) {
        when (action) {
            EditProfileUiAction.Fetch -> fetchUser()
            is EditProfileUiAction.ImageChanged -> setProfileImageByteArray(action.value)
            is EditProfileUiAction.Save -> saveProfile(
                action.username,
                action.firstName,
                action.lastName,
                action.birthDate,
                action.phone,
            )
            EditProfileUiAction.ResetSaveState -> resetSaveState()
        }
    }

    // 🌟 ปรับปรุง: ใส่การเปิด/ปิด _isLoading ให้ชัดเจน
    fun fetchUser() {
        if (fetchJob?.isActive == true) return
        val job = screenModelScope.launch {
            _isLoading.value = true // เริ่มหมุนโหลด
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                repository.getUser().onSuccess {
                    _userState.value = it
                    _uiState.update { state ->
                        state.copy(data = (state.data ?: EditProfileUiData()).copy(user = it))
                    }
                    logger.debug("Edit profile data fetched")
                }.onFailure { error ->
                    logger.warn("Edit profile fetch failed", error)
                    val appError = AppError.Unknown(error)
                    _uiState.update { it.copy(error = appError) }
                    _effects.tryEmit(EditProfileUiEffect.ShowError(appError))
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                logger.warn("Edit profile fetch failed unexpectedly", error)
                val appError = AppError.Unknown(error)
                _uiState.update { it.copy(error = appError) }
                _effects.tryEmit(EditProfileUiEffect.ShowError(appError))
            } finally {
                _isLoading.value = false
                _uiState.update { it.copy(isLoading = false) }
            }
        }
        fetchJob = job
        job.invokeOnCompletion { if (fetchJob === job) fetchJob = null }
    }

    fun saveProfile(username: String, firstName: String, lastName: String, birthDate: String, phone: String) {
        if (_isSaving.value || saveJob?.isActive == true) return
        _isSaving.value = true
        _saveSuccess.value = false
        _uiState.update { state ->
            state.copy(data = (state.data ?: EditProfileUiData()).copy(isSaving = true), error = null)
        }

        // 1. ดึง ByteArray ที่เก็บไว้ตอน User เลือกรูป
        val currentImage = _profileImageByteArray.value

        val request = UpdateUserDataRequest(
            username = username,
            firstName = firstName,
            lastName = lastName,
            birthday = birthDate,
            phoneNumber = phone,
            profileImage = currentImage // 2. แนบรูปส่งไปด้วย (หรือ null ถ้าไม่ได้เปลี่ยนรูป)
        )

        val job = screenModelScope.launch {
            try {
                // The data:profile repository owns multipart image transport.
                repository.updateUserData(request).onSuccess {
                    _saveSuccess.value = true
                    _uiState.update { state ->
                        state.copy(data = (state.data ?: EditProfileUiData()).copy(isSaving = false, saveSuccess = true))
                    }
                    _effects.tryEmit(EditProfileUiEffect.Saved)
                }.onFailure { error ->
                    logger.warn("Save profile failed", error)
                    val appError = AppError.Unknown(error)
                    _uiState.update { state ->
                        state.copy(
                            data = (state.data ?: EditProfileUiData()).copy(isSaving = false),
                            error = appError,
                        )
                    }
                    _effects.tryEmit(EditProfileUiEffect.ShowError(appError))
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                logger.warn("Save profile failed unexpectedly", error)
                val appError = AppError.Unknown(error)
                _uiState.update { state ->
                    state.copy(
                        data = (state.data ?: EditProfileUiData()).copy(isSaving = false),
                        error = appError,
                    )
                }
                _effects.tryEmit(EditProfileUiEffect.ShowError(appError))
            } finally {
                _isSaving.value = false
                _uiState.update { state ->
                    state.copy(data = (state.data ?: EditProfileUiData()).copy(isSaving = false))
                }
            }
        }
        saveJob = job
        job.invokeOnCompletion { if (saveJob === job) saveJob = null }
    }

    // ฟังก์ชันล้างสถานะ เพื่อป้องกันบั๊กเด้งออกเองเวลาเข้าหน้าใหม่
    fun resetSaveState() {
        _saveSuccess.value = false
        _isSaving.value = false
        _uiState.update { state ->
            state.copy(data = (state.data ?: EditProfileUiData()).copy(isSaving = false, saveSuccess = false))
        }
    }
}
