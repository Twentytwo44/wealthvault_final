package com.wealthvault.financiallist.ui.shareasset

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.core.architecture.AppError
import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.architecture.UiAction
import com.wealthvault.core.architecture.UiEffect
import com.wealthvault.core.architecture.UiState
import com.wealthvault.core.architecture.toAppError
import com.wealthvault.domain.social.UnshareRepository
import com.wealthvault.financiallist.ui.shareasset.model.FriendTargetModel
import com.wealthvault.financiallist.ui.shareasset.model.GroupTargetModel
import com.wealthvault.financiallist.ui.shareasset.model.ShareInfo
import com.wealthvault.financiallist.ui.shareasset.model.ShareTo
import com.wealthvault.financiallist.ui.shareasset.usecase.GetShareAssetUseCase
import com.wealthvault.domain.social.ShareItems
import com.wealthvault.domain.social.ShareTarget
import com.wealthvault.domain.social.ShareItemRepository
// 🌟 นำเข้าคลาส Unshare ของคุณแชมป์ด้วยนะครับ (ถ้าชื่อเปลี่ยนไป ให้ปรับตามของจริงได้เลย)
// import com.wealthvault.share.data.UnshareRepositoryImpl
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ShareAssetState(
    val id: String = "",
    val type: String = "",
    val isLoading: Boolean = false,
    val error: AppError? = null,
)

sealed interface ShareUiAction : UiAction {
    data class Initialize(val id: String, val type: String) : ShareUiAction
    data class SetShareData(val value: ShareTo) : ShareUiAction
}

sealed interface ShareUiEffect : UiEffect {
    data object Saved : ShareUiEffect
    data class ShowError(val error: AppError) : ShareUiEffect
}

class ShareScreenModel(
    private val getShareAssetUseCase: GetShareAssetUseCase,
    private val shareRepository: ShareItemRepository,
    private val unshareRepository: UnshareRepository
) : ScreenModel {

    private val _formState = MutableStateFlow(ShareAssetState())
    val formState = _formState.asStateFlow()
    val uiState: StateFlow<UiState<ShareAssetState>> = _formState
        .map { state -> UiState(data = state, isLoading = state.isLoading, error = state.error) }
        .stateIn(screenModelScope, SharingStarted.Eagerly, UiState(data = ShareAssetState()))

    private val _effects = MutableSharedFlow<ShareUiEffect>(extraBufferCapacity = 1)
    val effects = _effects.asSharedFlow()
    private var fetchJob: Job? = null
    private var submitJob: Job? = null

    fun onAction(action: ShareUiAction) {
        when (action) {
            is ShareUiAction.Initialize -> initData(action.id, action.type)
            is ShareUiAction.SetShareData -> initShareData(action.value)
        }
    }

    private val _shareData = MutableStateFlow(ShareTo())

    private val _friendState = MutableStateFlow<List<FriendTargetModel>>(emptyList())
    val friendState = _friendState.asStateFlow()

    private val _groupState = MutableStateFlow<List<GroupTargetModel>>(emptyList())
    val groupState = _groupState.asStateFlow()

    private val _emailState = MutableStateFlow<List<ShareInfo>>(emptyList())
    val emailState = _emailState.asStateFlow()

    fun initData(id: String, type: String) {
        _formState.update { it.copy(id = id, type = type, error = null) }
        fetchData(id, type)
    }

    fun initShareData(shareTo: ShareTo) {
        _shareData.update {
            it.copy(
                friend = shareTo.friend,
                email = shareTo.email,
                group = shareTo.group,
            )
        }
    }

    private fun fetchData(id: String, type: String) {
        if (fetchJob?.isActive == true) return
        _formState.update { it.copy(isLoading = true, error = null) }
        val job = screenModelScope.launch {
            try {
                when (val result = getShareAssetUseCase(id, type)) {
                    is AppResult.Success -> {
                        _friendState.value = result.value.mappedFriends
                        _groupState.value = result.value.mappedGroups
                        _emailState.value = result.value.mappedEmails
                    }

                    is AppResult.Failure -> {
                        _formState.update { it.copy(error = result.error) }
                        _effects.tryEmit(ShareUiEffect.ShowError(result.error))
                    }
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                val appError = error.toAppError()
                _formState.update { it.copy(error = appError) }
                _effects.tryEmit(ShareUiEffect.ShowError(appError))
            } finally {
                _formState.update { it.copy(isLoading = false) }
            }
        }
        fetchJob = job
        job.invokeOnCompletion { if (fetchJob === job) fetchJob = null }
    }


    fun prepareUnshareItem(item: ShareInfo, currentAssetId: String, onReady: (ShareInfo) -> Unit) {
        screenModelScope.launch {
            try {
                var foundSharedItemId = ""

                if (item.typeData == "F") {
                    // ไปดึงรายการที่แชร์ให้เพื่อนคนนี้
                    when (val result = unshareRepository.getFriendItems(item.userId)) {
                        is AppResult.Success -> {
                            val match = result.value.find { it.assetDetail?.id == currentAssetId }
                            foundSharedItemId = match?.groupItemId ?: ""
                        }

                        is AppResult.Failure -> {
                            _formState.update { it.copy(error = result.error) }
                            _effects.tryEmit(ShareUiEffect.ShowError(result.error))
                            return@launch
                        }
                    }
                } else if (item.typeData == "G") {
                    // ไปดึงรายการที่แชร์ให้กลุ่มนี้
                    when (val result = unshareRepository.getGroupItems(item.userId)) {
                        is AppResult.Success -> {
                            val match = result.value.find { it.assetDetail?.id == currentAssetId }
                            foundSharedItemId = match?.groupItemId ?: ""
                        }

                        is AppResult.Failure -> {
                            _formState.update { it.copy(error = result.error) }
                            _effects.tryEmit(ShareUiEffect.ShowError(result.error))
                            return@launch
                        }
                    }
                }

                if (foundSharedItemId.isNotEmpty()) {
                    // 🌟 ถ้าเจอ ให้เอา sharedItemId ยัดใส่ Object แล้วส่งกลับไป
                    val preparedItem = item.copy(sharedItemId = foundSharedItemId)
                    onReady(preparedItem)
                } else {
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                val appError = error.toAppError()
                _formState.update { it.copy(error = appError) }
                _effects.tryEmit(ShareUiEffect.ShowError(appError))
            }
        }
    }

    fun submitShare(id: String, type: String, unshareList: List<ShareInfo>, onSuccess: () -> Unit = {}) {
        if (submitJob?.isActive == true) return
        val shareToData = _shareData.value
        _formState.update { it.copy(isLoading = true, error = null) }
        val job = screenModelScope.launch {
            try {
                // 🌟 1. ยิง API ยกเลิกการแชร์ (Unshare) ให้ครบทุกคนในถังขยะ
                if (unshareList.isNotEmpty()) {
                    val unshareResults = unshareList.map { item ->
                        async {
                            when {
                                item.typeData == "F" && item.sharedItemId.isNotBlank() ->
                                    unshareRepository.unshareFriend(item.sharedItemId)

                                item.typeData == "G" && item.sharedItemId.isNotBlank() ->
                                    unshareRepository.unshareGroup(item.sharedItemId)

                                else -> AppResult.Success(Unit)
                            }
                        }
                    }.awaitAll()
                    val unshareFailure = unshareResults.filterIsInstance<AppResult.Failure>().firstOrNull()
                    if (unshareFailure != null) {
                        _formState.update { it.copy(error = unshareFailure.error) }
                        _effects.tryEmit(ShareUiEffect.ShowError(unshareFailure.error))
                        return@launch
                    }
                }

                // 🌟 2. ยิง API อัปเดต/เพิ่ม คนที่แชร์ใหม่
                val requestShareItem = ShareItems(
                    itemIds = id,
                    itemTypes = type,
                    emails = shareToData.email.map { ShareTarget(id = it.userId, shareAt = it.apiDate) },
                    friends = shareToData.friend.map { ShareTarget(id = it.userId, shareAt = it.apiDate) },
                    groups = shareToData.group.map { ShareTarget(id = it.userId, shareAt = it.apiDate) }
                )

                val shareResult = shareRepository.shareItem(requestShareItem)

                when (shareResult) {
                    is AppResult.Success -> {
                        _effects.tryEmit(ShareUiEffect.Saved)
                        onSuccess()
                    }
                    is AppResult.Failure -> {
                        _formState.update { it.copy(error = shareResult.error) }
                        _effects.tryEmit(ShareUiEffect.ShowError(shareResult.error))
                    }
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                val appError = error.toAppError()
                _formState.update { it.copy(error = appError) }
                _effects.tryEmit(ShareUiEffect.ShowError(appError))
            } finally {
                _formState.update { it.copy(isLoading = false) }
            }
        }
        submitJob = job
        job.invokeOnCompletion { if (submitJob === job) submitJob = null }
    }
}
