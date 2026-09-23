package com.wealthvault.social.ui.manage_shared

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.domain.social.ShareableItem
import com.wealthvault.domain.social.ShareItems
import com.wealthvault.domain.social.ShareTarget
import com.wealthvault.domain.social.SocialRepository
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

data class SharedAssetUiData(
    val assets: List<ShareableItem> = emptyList(),
    val isShareSuccess: Boolean = false,
    val errorMessage: String? = null,
)

sealed interface SharedAssetUiAction : UiAction {
    data class Refresh(val targetId: String, val isGroup: Boolean) : SharedAssetUiAction
    data class Submit(val targetId: String, val isGroup: Boolean, val selectedIds: List<String>) : SharedAssetUiAction
}

sealed interface SharedAssetUiEffect : UiEffect {
    data object Saved : SharedAssetUiEffect
    data class ShowError(val error: AppError) : SharedAssetUiEffect
}

class SharedAssetScreenModel(
    private val repository: SocialRepository
) : ScreenModel {

    private val _assetList = MutableStateFlow<List<ShareableItem>>(emptyList())
    val assetList: StateFlow<List<ShareableItem>> = _assetList.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isShareSuccess = MutableStateFlow(false)
    val isShareSuccess: StateFlow<Boolean> = _isShareSuccess.asStateFlow()

    private val _uiState = MutableStateFlow(UiState(data = SharedAssetUiData()))
    val uiState: StateFlow<UiState<SharedAssetUiData>> = _uiState.asStateFlow()
    private val _effects = MutableSharedFlow<SharedAssetUiEffect>(extraBufferCapacity = 1)
    val effects = _effects.asSharedFlow()

    private var fetchJob: Job? = null
    private var mutationJob: Job? = null

    fun onAction(action: SharedAssetUiAction) {
        when (action) {
            is SharedAssetUiAction.Refresh -> fetchItemsToShare(action.targetId, action.isGroup)
            is SharedAssetUiAction.Submit -> submitShareAssets(action.targetId, action.isGroup, action.selectedIds)
        }
    }

    private fun syncUiState(isLoading: Boolean = _isLoading.value, error: AppError? = _uiState.value.error) {
        _uiState.value = UiState(
            data = SharedAssetUiData(_assetList.value, _isShareSuccess.value, _errorMessage.value),
            isLoading = isLoading,
            error = error,
        )
    }

    // 🌟 ฟังก์ชันโหลดรายการสินทรัพย์ที่แชร์ได้
    // ใน SharedAssetScreenModel.kt

    fun fetchItemsToShare(targetId: String, isGroup: Boolean) {
        if (fetchJob?.isActive == true) return
        _isLoading.value = true
        val job = screenModelScope.launch {
            syncUiState(isLoading = true, error = null)
            try {
                repository.getItemsToShare(targetId, isGroup).onSuccess { list ->
                    _assetList.value = list
                    syncUiState(isLoading = false, error = null)
                }.onFailure { error ->
                    _errorMessage.value = error.message
                    val appError = error.toAppError()
                    syncUiState(isLoading = false, error = appError)
                    _effects.tryEmit(SharedAssetUiEffect.ShowError(appError))
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                _errorMessage.value = error.message
                val appError = error.toAppError()
                syncUiState(isLoading = false, error = appError)
                _effects.tryEmit(SharedAssetUiEffect.ShowError(appError))
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
    // 🌟 ฟังก์ชันบันทึกการแชร์ (แบบลูปส่งทีละรายการ)
    fun submitShareAssets(targetId: String, isGroup: Boolean, selectedIds: List<String>) {
        if (_isLoading.value || mutationJob?.isActive == true) return
        _isLoading.value = true
        val job = screenModelScope.launch {
            _isShareSuccess.value = false
            _errorMessage.value = null
            syncUiState(isLoading = true, error = null)
            try {
                val selectedAssets = _assetList.value.filter { it.id in selectedIds }
                val targetList = listOf(ShareTarget(id = targetId))
                var isAllSuccess = true
                var lastErrorMsg: String? = null

                for (asset in selectedAssets) {
                    val currentId = asset.id ?: continue
                    val currentType = asset.type ?: continue
                    val request = ShareItems(
                        itemIds = currentId,
                        itemTypes = currentType,
                        friends = if (!isGroup) targetList else null,
                        groups = if (isGroup) targetList else null,
                        emails = null,
                    )
                    val result = repository.submitShareItems(request)
                    if (result.isFailure) {
                        isAllSuccess = false
                        lastErrorMsg = result.exceptionOrNull()?.message
                        break
                    }
                }

                if (isAllSuccess) {
                    _isShareSuccess.value = true
                    syncUiState(isLoading = false, error = null)
                    _effects.tryEmit(SharedAssetUiEffect.Saved)
                } else {
                    _isShareSuccess.value = false
                    _errorMessage.value = lastErrorMsg ?: "เกิดข้อผิดพลาดในการแชร์บางรายการ"
                    val appError = IllegalStateException(_errorMessage.value).toAppError()
                    syncUiState(isLoading = false, error = appError)
                    _effects.tryEmit(SharedAssetUiEffect.ShowError(appError))
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                _errorMessage.value = error.message
                val appError = error.toAppError()
                syncUiState(isLoading = false, error = appError)
                _effects.tryEmit(SharedAssetUiEffect.ShowError(appError))
            } finally {
                _isLoading.value = false
                syncUiState(isLoading = false)
            }
        }
        mutationJob = job
        job.invokeOnCompletion { if (mutationJob === job) mutationJob = null }
    }
}
