package com.wealthvault.social.ui.manage_shared

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.domain.auth.SessionManager
import com.wealthvault.domain.social.ShareGroup
import com.wealthvault.domain.social.SocialRepository
import com.wealthvault.core.observability.AppLogger
import com.wealthvault.core.observability.platformLogger
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
import kotlinx.coroutines.flow.firstOrNull // 🌟 นำเข้า firstOrNull
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

data class SharedAssetManageUiData(val assets: List<ShareGroup> = emptyList())

sealed interface SharedAssetManageUiAction : UiAction {
    data class Refresh(val targetId: String, val isGroup: Boolean) : SharedAssetManageUiAction
}

sealed interface SharedAssetManageUiEffect : UiEffect {
    data class ShowError(val error: AppError) : SharedAssetManageUiEffect
}

class SharedAssetManageScreenModel(
    private val repository: SocialRepository,
    private val sessionManager: SessionManager,
    private val logger: AppLogger = platformLogger(),
) : ScreenModel {

    private val _assetList = MutableStateFlow<List<ShareGroup>>(emptyList())
    val assetList: StateFlow<List<ShareGroup>> = _assetList.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _uiState = MutableStateFlow(UiState(data = SharedAssetManageUiData(), isLoading = true))
    val uiState: StateFlow<UiState<SharedAssetManageUiData>> = _uiState.asStateFlow()
    private val _effects = MutableSharedFlow<SharedAssetManageUiEffect>(extraBufferCapacity = 1)
    val effects = _effects.asSharedFlow()

    private var fetchJob: Job? = null
    private var mutationJob: Job? = null

    fun onAction(action: SharedAssetManageUiAction) {
        when (action) {
            is SharedAssetManageUiAction.Refresh -> fetchSharedAssets(action.targetId, action.isGroup)
        }
    }

    private fun syncUiState(isLoading: Boolean = _isLoading.value, error: AppError? = _uiState.value.error) {
        _uiState.value = UiState(data = SharedAssetManageUiData(_assetList.value), isLoading = isLoading, error = error)
    }

    // 🌟 ลบ mocID ทิ้งไปเลยครับ!

    // 🌟 ฟังก์ชันดึงข้อมูลทรัพย์สินที่แชร์
    fun fetchSharedAssets(targetId: String, isGroup: Boolean) {
        if (fetchJob?.isActive == true) return
        _isLoading.value = true
        val job = screenModelScope.launch {
            syncUiState(isLoading = true, error = null)
            try {
                // 🌟 3. อ่านค่า User ID จากเครื่อง
                val currentUserId = sessionManager.getUserId.firstOrNull() ?: ""
                if (isGroup) {
                    val result = repository.getShareGroupItems(targetId)
                    when (result) {
                        is com.wealthvault.core.architecture.AppResult.Success -> {
                            _assetList.value = result.value.filter { it.sharedBy == currentUserId }
                        }
                        is com.wealthvault.core.architecture.AppResult.Failure -> {
                            val appError = result.error
                            syncUiState(isLoading = false, error = appError)
                            _effects.tryEmit(SharedAssetManageUiEffect.ShowError(appError))
                        }
                    }
                } else {
                    val result = repository.getShareFriendItems(targetId)
                    when (result) {
                        is com.wealthvault.core.architecture.AppResult.Success -> {
                            _assetList.value = result.value.map { friendData ->
                                ShareGroup(
                                    groupItemId = friendData.groupItemId,
                                    sharedBy = friendData.sharedBy,
                                    sharedAt = friendData.sharedAt,
                                    type = friendData.type,
                                    assetDetail = friendData.assetDetail,
                                )
                            }.filter { it.sharedBy == currentUserId }
                        }
                        is com.wealthvault.core.architecture.AppResult.Failure -> {
                            val appError = result.error
                            syncUiState(isLoading = false, error = appError)
                            _effects.tryEmit(SharedAssetManageUiEffect.ShowError(appError))
                        }
                    }
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                val appError = error.toAppError()
                syncUiState(isLoading = false, error = appError)
                _effects.tryEmit(SharedAssetManageUiEffect.ShowError(appError))
            } finally {
                _isLoading.value = false
                syncUiState(isLoading = false)
            }
        }
        fetchJob = job
        job.invokeOnCompletion {
            if (fetchJob === job) fetchJob = null
        }
    }

    // 🌟 ฟังก์ชันยกเลิกการแชร์ (ลบข้อมูล)
    fun unShareAsset(assetId: String, isGroup: Boolean) {
        if (mutationJob?.isActive == true || _isLoading.value) return
        _isLoading.value = true
        val job = screenModelScope.launch {
            syncUiState(isLoading = true, error = null)
            try {
                repository.unShareAsset(assetId, isGroup)
                    .onSuccess {
                        _assetList.value = _assetList.value.filterNot { it.groupItemId == assetId }
                        syncUiState(isLoading = false, error = null)
                    }
                    .onFailure { error ->
                        logger.warn("Unshare asset failed", error)
                        val appError = error.toAppError()
                        syncUiState(isLoading = false, error = appError)
                        _effects.tryEmit(SharedAssetManageUiEffect.ShowError(appError))
                    }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                val appError = error.toAppError()
                syncUiState(isLoading = false, error = appError)
                _effects.tryEmit(SharedAssetManageUiEffect.ShowError(appError))
            } finally {
                _isLoading.value = false
                syncUiState(isLoading = false)
            }
        }
        mutationJob = job
        job.invokeOnCompletion {
            if (mutationJob === job) mutationJob = null
        }
    }
}
