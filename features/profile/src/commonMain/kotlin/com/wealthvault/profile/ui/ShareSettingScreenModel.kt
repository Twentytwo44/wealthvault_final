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
import com.wealthvault.domain.profile.CloseFriendData
import com.wealthvault.domain.profile.FriendData
import com.wealthvault.domain.profile.ProfileRepository
import com.wealthvault.domain.profile.UpdateUserDataRequest
import com.wealthvault.domain.profile.UserData
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

data class ShareSettingUiData(
    val user: UserData? = null,
    val closeFriends: List<CloseFriendData> = emptyList(),
    val allFriends: List<FriendData> = emptyList(),
    val isSaving: Boolean = false,
)

sealed interface ShareSettingUiAction : UiAction {
    data object Fetch : ShareSettingUiAction
    data object FetchAllFriends : ShareSettingUiAction
    data class Update(val sharedEnabled: Boolean, val sharedAge: Int) : ShareSettingUiAction
    data class RemoveCloseFriend(val friendId: String) : ShareSettingUiAction
    data class AddCloseFriends(val friendIds: List<String>) : ShareSettingUiAction
}

sealed interface ShareSettingUiEffect : UiEffect {
    data object Saved : ShareSettingUiEffect
    data class ShowError(val error: AppError) : ShareSettingUiEffect
}

// ==========================================
// 🌟 1. Screen Model (ฝั่งจัดการ Logic)
// ==========================================
class ShareSettingScreenModel(
    private val repository: ProfileRepository,
    private val logger: AppLogger = platformLogger(),
) : ScreenModel {

    private val _uiState = MutableStateFlow<UiState<ShareSettingUiData>>(
        UiState(data = ShareSettingUiData(), isLoading = true),
    )
    val uiState: StateFlow<UiState<ShareSettingUiData>> = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<ShareSettingUiEffect>(extraBufferCapacity = 1)
    val effects = _effects.asSharedFlow()

    // 🌟 เพิ่ม State สำหรับสถานะ Loading
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _userState = MutableStateFlow<UserData?>(null)
    val userState = _userState.asStateFlow()

    private val _closeFriends = MutableStateFlow<List<CloseFriendData>>(emptyList())
    val closeFriends = _closeFriends.asStateFlow()

    private val _allFriends = MutableStateFlow<List<FriendData>>(emptyList())
    val allFriends = _allFriends.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving = _isSaving.asStateFlow()

    private var fetchJob: Job? = null
    private var allFriendsJob: Job? = null
    private var saveJob: Job? = null
    private var closeFriendJob: Job? = null

    fun onAction(action: ShareSettingUiAction) {
        when (action) {
            ShareSettingUiAction.Fetch -> fetchShareSettingData()
            ShareSettingUiAction.FetchAllFriends -> fetchAllFriends()
            is ShareSettingUiAction.Update -> updateShareSettings(action.sharedEnabled, action.sharedAge)
            is ShareSettingUiAction.RemoveCloseFriend -> removeCloseFriend(action.friendId)
            is ShareSettingUiAction.AddCloseFriends -> addCloseFriends(action.friendIds)
        }
    }

    private fun updateUiState(
        isLoading: Boolean = _uiState.value.isLoading,
        error: AppError? = _uiState.value.error,
        isSaving: Boolean = _isSaving.value,
    ) {
        _uiState.update {
            it.copy(
                data = ShareSettingUiData(
                    user = _userState.value,
                    closeFriends = _closeFriends.value,
                    allFriends = _allFriends.value,
                    isSaving = isSaving,
                ),
                isLoading = isLoading,
                error = error,
            )
        }
    }

    // 🌟 ดึงข้อมูล User และ Close Friends แบบต่อแถว (Sequential)
    fun fetchShareSettingData() {
        if (fetchJob?.isActive == true) return
        val job = screenModelScope.launch {
            _isLoading.value = true
            updateUiState(isLoading = true, error = null)

            try {
                // 1. ดึง User ก่อน
                repository.getUser()
                    .onSuccess { userData ->
                        _userState.value = userData
                        updateUiState()
                    }
                    .onFailure { error ->
                        logger.warn("Fetch user failed", error)
                        updateUiState(error = AppError.Unknown(error))
                    }

                // 2. ดึง Close Friends ต่อ
                repository.getCloseFriends()
                    .onSuccess { friends ->
                        _closeFriends.value = friends
                        logger.debug("Fetched close friends: ${friends.size}")
                        updateUiState()
                    }
                    .onFailure { error ->
                        logger.warn("Fetch close friends failed", error)
                        val appError = AppError.Unknown(error)
                        updateUiState(error = appError)
                        _effects.tryEmit(ShareSettingUiEffect.ShowError(appError))
                    }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                logger.warn("Fetch share settings failed unexpectedly", error)
                val appError = AppError.Unknown(error)
                updateUiState(error = appError)
                _effects.tryEmit(ShareSettingUiEffect.ShowError(appError))
            } finally {
                _isLoading.value = false
                updateUiState(isLoading = false)
            }
        }
        fetchJob = job
        job.invokeOnCompletion { if (fetchJob === job) fetchJob = null }
    }

    // ดึงเพื่อนทั้งหมดเพื่อมาให้กดเลือก
    fun fetchAllFriends() {
        if (allFriendsJob?.isActive == true) return
        val job = screenModelScope.launch {
            try {
                repository.getAllFriends()
                    .onSuccess {
                        _allFriends.value = it
                        updateUiState()
                    }
                    .onFailure { error ->
                        logger.warn("Fetch all friends failed", error)
                        val appError = AppError.Unknown(error)
                        updateUiState(error = appError)
                        _effects.tryEmit(ShareSettingUiEffect.ShowError(appError))
                    }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                logger.warn("Fetch all friends failed unexpectedly", error)
                val appError = AppError.Unknown(error)
                updateUiState(error = appError)
                _effects.tryEmit(ShareSettingUiEffect.ShowError(appError))
            }
        }
        allFriendsJob = job
        job.invokeOnCompletion { if (allFriendsJob === job) allFriendsJob = null }
    }

    fun updateShareSettings(sharedEnabled: Boolean, sharedAge: Int) {
        if (_isSaving.value || saveJob?.isActive == true) return
        val currentUser = _userState.value ?: return
        _isSaving.value = true
        updateUiState(isSaving = true, error = null)

        val formattedBirthday = currentUser.birthday?.take(10)

        val request = UpdateUserDataRequest(
            username = currentUser.username ?: "",
            firstName = currentUser.firstName ?: "",
            lastName = currentUser.lastName ?: "",
            birthday = formattedBirthday ?: "",
            phoneNumber = currentUser.phoneNumber ?: "",
            sharedEnabled = sharedEnabled,
            sharedAge = sharedAge
        )

        val job = screenModelScope.launch {
            try {
                repository.updateUserData(request).onSuccess {
                    logger.info("Update sharing settings succeeded")
                    _userState.value = currentUser.copy(
                        shareEnabled = sharedEnabled,
                        sharedAge = sharedAge
                    )
                    updateUiState(isSaving = false)
                    _effects.tryEmit(ShareSettingUiEffect.Saved)
                }.onFailure { error ->
                    logger.warn("Update sharing settings failed", error)
                    val appError = AppError.Unknown(error)
                    updateUiState(isSaving = false, error = appError)
                    _effects.tryEmit(ShareSettingUiEffect.ShowError(appError))
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                logger.warn("Update sharing settings failed unexpectedly", error)
                val appError = AppError.Unknown(error)
                updateUiState(isSaving = false, error = appError)
                _effects.tryEmit(ShareSettingUiEffect.ShowError(appError))
            } finally {
                _isSaving.value = false
                updateUiState(isSaving = false)
            }
        }
        saveJob = job
        job.invokeOnCompletion { if (saveJob === job) saveJob = null }
    }

    fun removeCloseFriend(friendId: String) {
        if (closeFriendJob?.isActive == true) return
        val currentFriends = _closeFriends.value
        _closeFriends.value = currentFriends.filter { it.id != friendId }
        updateUiState()

        val job = screenModelScope.launch {
            try {
                repository.setCloseFriend(friendId = friendId, isClose = false)
                    .onSuccess {
                        logger.info("Remove close friend succeeded")
                    }
                    .onFailure { error ->
                        logger.warn("Remove close friend failed", error)
                        _closeFriends.value = currentFriends // Rollback
                        updateUiState(error = AppError.Unknown(error))
                    }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                logger.warn("Remove close friend failed unexpectedly", error)
                _closeFriends.value = currentFriends
                updateUiState(error = AppError.Unknown(error))
            }
        }
        closeFriendJob = job
        job.invokeOnCompletion { if (closeFriendJob === job) closeFriendJob = null }
    }

    fun addCloseFriends(friendIds: List<String>) {
        if (friendIds.isEmpty() || closeFriendJob?.isActive == true) return
        val job = screenModelScope.launch {
            try {
                friendIds.forEach { id ->
                    repository.setCloseFriend(id, true)
                        .onFailure { error ->
                            val appError = AppError.Unknown(error)
                            logger.warn("Add close friend failed", error)
                            updateUiState(error = appError)
                            _effects.tryEmit(ShareSettingUiEffect.ShowError(appError))
                        }
                }
                // เรียกฟังก์ชันนี้อีกครั้งเพื่อดึงข้อมูลใหม่มาโชว์
                fetchShareSettingData()
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                logger.warn("Add close friend failed unexpectedly", error)
                val appError = AppError.Unknown(error)
                updateUiState(error = appError)
                _effects.tryEmit(ShareSettingUiEffect.ShowError(appError))
            }
        }
        closeFriendJob = job
        job.invokeOnCompletion { if (closeFriendJob === job) closeFriendJob = null }
    }
}
