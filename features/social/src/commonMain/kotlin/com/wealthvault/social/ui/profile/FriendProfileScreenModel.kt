package com.wealthvault.social.ui.profile

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.domain.portfolio.BankAccountData
import com.wealthvault.domain.portfolio.BuildingIdData
import com.wealthvault.domain.portfolio.CashIdData
import com.wealthvault.domain.portfolio.InsuranceIdData
import com.wealthvault.domain.portfolio.InvestmentIdData
import com.wealthvault.domain.portfolio.LandIdData
import com.wealthvault.domain.portfolio.LiabilityIdData
import com.wealthvault.core.observability.AppLogger
import com.wealthvault.core.observability.platformLogger
import com.wealthvault.domain.social.SocialRepository
import com.wealthvault.domain.social.FriendProfile
import com.wealthvault.core.architecture.AppError
import com.wealthvault.core.architecture.UiAction
import com.wealthvault.core.architecture.UiEffect
import com.wealthvault.core.architecture.UiState
import com.wealthvault.core.architecture.toAppError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.Job
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

data class FriendProfileUiData(
    val profile: FriendProfile? = null,
    val isSuccess: Boolean = false,
    val isRemoveSuccess: Boolean = false,
    val isAlreadySent: Boolean = false,
)

sealed interface FriendProfileUiAction : UiAction {
    data class Refresh(val friendId: String) : FriendProfileUiAction
    data class Add(val targetId: String) : FriendProfileUiAction
    data class Remove(val targetId: String) : FriendProfileUiAction
}

sealed interface FriendProfileUiEffect : UiEffect {
    data object Saved : FriendProfileUiEffect
    data class ShowError(val error: AppError) : FriendProfileUiEffect
}

class FriendProfileScreenModel(
    private val repository: SocialRepository,
    private val logger: AppLogger = platformLogger(),
) : ScreenModel {

    private val _profileData = MutableStateFlow<FriendProfile?>(null)
    val profileData: StateFlow<FriendProfile?> = _profileData.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // 🌟 ใช้ State ตัวเดียวคุมความสำเร็จ ทั้งเพิ่มและลบเพื่อน
    private val _isSuccess = MutableStateFlow(false)
    val isSuccess: StateFlow<Boolean> = _isSuccess.asStateFlow()


    // 🌟 ฟังก์ชันเพิ่มเพื่อน
    private val _isRemoveSuccess = MutableStateFlow(false)
    val isRemoveSuccess = _isRemoveSuccess.asStateFlow()

    private val _isAlreadySent = MutableStateFlow(false)
    val isAlreadySent: StateFlow<Boolean> = _isAlreadySent.asStateFlow()

    private var fetchJob: Job? = null
    private var mutationJob: Job? = null

    private val _uiState = MutableStateFlow(UiState(data = FriendProfileUiData(), isLoading = true))
    val uiState: StateFlow<UiState<FriendProfileUiData>> = _uiState.asStateFlow()
    private val _effects = MutableSharedFlow<FriendProfileUiEffect>(extraBufferCapacity = 1)
    val effects = _effects.asSharedFlow()

    fun onAction(action: FriendProfileUiAction) {
        when (action) {
            is FriendProfileUiAction.Refresh -> fetchProfile(action.friendId)
            is FriendProfileUiAction.Add -> addFriend(action.targetId)
            is FriendProfileUiAction.Remove -> removeFriend(action.targetId)
        }
    }

    private fun syncUiState(isLoading: Boolean = _isLoading.value, error: AppError? = _uiState.value.error) {
        _uiState.value = UiState(
            data = FriendProfileUiData(_profileData.value, _isSuccess.value, _isRemoveSuccess.value, _isAlreadySent.value),
            isLoading = isLoading,
            error = error,
        )
    }

    fun addFriend(targetId: String) {
        if (mutationJob?.isActive == true || _isLoading.value) return
        _isLoading.value = true
        val job = screenModelScope.launch {
            _isSuccess.value = false
            _isAlreadySent.value = false // รีเซ็ตค่าก่อนเริ่ม
            syncUiState(isLoading = true, error = null)

            try {
                repository.addFriend(targetId).onSuccess {
                    _isSuccess.value = true
                    logger.info("Send friend request succeeded")
                    syncUiState(isLoading = false, error = null)
                    _effects.tryEmit(FriendProfileUiEffect.Saved)
                }.onFailure { error ->
                    // 🌟 เช็คว่า Error Message มีคำว่า friend request already sent หรือไม่
                    if (error.message?.contains("friend request already sent") == true) {
                        _isAlreadySent.value = true
                        syncUiState(isLoading = false, error = null)
                    } else {
                        _isAlreadySent.value = false
                        logger.warn("Send friend request failed", error)
                        val appError = error.toAppError()
                        syncUiState(isLoading = false, error = appError)
                        _effects.tryEmit(FriendProfileUiEffect.ShowError(appError))
                    }
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                logger.warn("Send friend request failed unexpectedly", error)
                val appError = error.toAppError()
                syncUiState(isLoading = false, error = appError)
                _effects.tryEmit(FriendProfileUiEffect.ShowError(appError))
            } finally {
                _isLoading.value = false
            }
        }
        mutationJob = job
        job.invokeOnCompletion {
            if (mutationJob === job) mutationJob = null
        }
    }

    // --- ใน FriendProfileScreenModel.kt ---

    fun fetchProfile(friendId: String) {
        if (fetchJob?.isActive == true) return
        val job = screenModelScope.launch {
            _isLoading.value = true

            // 🌟 รีเซ็ตสถานะทั้งหมดก่อนโหลดใหม่
            _isSuccess.value = false
            _isRemoveSuccess.value = false
            _isAlreadySent.value = false
            syncUiState(isLoading = true, error = null)

            try {
                repository.getFriendProfile(friendId).onSuccess { data ->
                    _profileData.value = data
                    syncUiState(isLoading = false, error = null)
                }.onFailure { error ->
                    _profileData.value = null
                    val appError = error.toAppError()
                    syncUiState(isLoading = false, error = appError)
                    _effects.tryEmit(FriendProfileUiEffect.ShowError(appError))
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                val appError = error.toAppError()
                _profileData.value = null
                syncUiState(isLoading = false, error = appError)
                _effects.tryEmit(FriendProfileUiEffect.ShowError(appError))
            } finally {
                _isLoading.value = false
            }
        }
        fetchJob = job
        job.invokeOnCompletion {
            if (fetchJob === job) fetchJob = null
        }
    }

    fun removeFriend(targetId: String) {
        if (mutationJob?.isActive == true || _isLoading.value) return
        _isLoading.value = true
        val job = screenModelScope.launch {
            _isRemoveSuccess.value = false
            syncUiState(isLoading = true, error = null)
            try {
                repository.removeFriend(targetId).onSuccess {
                    // 🌟 เมื่อ API ลบสำเร็จ ตัวแปรนี้จะไปสะกิด LaunchedEffect ใน Screen ให้รีโหลด
                    _isRemoveSuccess.value = true
                    logger.info("Remove friend succeeded")
                    syncUiState(isLoading = false, error = null)
                    _effects.tryEmit(FriendProfileUiEffect.Saved)
                }.onFailure { error ->
                    _isRemoveSuccess.value = false
                    logger.warn("Remove friend failed", error)
                    val appError = error.toAppError()
                    syncUiState(isLoading = false, error = appError)
                    _effects.tryEmit(FriendProfileUiEffect.ShowError(appError))
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                _isRemoveSuccess.value = false
                logger.warn("Remove friend failed unexpectedly", error)
                val appError = error.toAppError()
                syncUiState(isLoading = false, error = appError)
                _effects.tryEmit(FriendProfileUiEffect.ShowError(appError))
            } finally {
                _isLoading.value = false
            }
        }
        mutationJob = job
        job.invokeOnCompletion {
            if (mutationJob === job) mutationJob = null
        }
    }
    // --- ฟังก์ชันโหลดสินทรัพย์ต่างๆ คงเดิม ---
    suspend fun getAccountById(id: String): BankAccountData? {
        return repository.getAccountById(id).getOrNull()
    }

    suspend fun getBuildingById(id: String): BuildingIdData? {
        return repository.getBuildingById(id).getOrNull()
    }

    suspend fun getCashById(id: String): CashIdData? {
        return repository.getCashById(id).getOrNull()
    }

    suspend fun getInsuranceById(id: String): InsuranceIdData? {
        return repository.getInsuranceById(id).getOrNull()
    }

    suspend fun getInvestmentById(id: String): InvestmentIdData? {
        return repository.getInvestmentById(id).getOrNull()
    }

    suspend fun getLandById(id: String): LandIdData? {
        return repository.getLandById(id).getOrNull()
    }

    suspend fun getLiabilityById(id: String): LiabilityIdData? {
        return repository.getLiabilityById(id).getOrNull()
    }
}
