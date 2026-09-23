package com.wealthvault.`financial-asset`.ui.share

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.core.architecture.FormEffect
import com.wealthvault.core.architecture.UiState
import com.wealthvault.core.architecture.UiStateHolder
import com.wealthvault.financialcommon.architecture.SummaryAction
import com.wealthvault.domain.profile.FriendDirectoryRepository
import com.wealthvault.domain.social.GroupDirectoryRepository
import com.wealthvault.domain.social.ShareTo
import com.wealthvault.domain.profile.FriendData
import com.wealthvault.domain.social.GroupSummary
import com.wealthvault.core.architecture.toAppError
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ShareAssetState<T>(
    val request: T? = null,
    val shareTo: ShareTo? = null,
    val isLoading: Boolean = false
)
class ShareAssetScreenModel<T>(
    private val friendRepository: FriendDirectoryRepository,
    private val groupRepository: GroupDirectoryRepository,
) : ScreenModel {

    private val _formState = MutableStateFlow(ShareAssetState<T>())
    val formState = _formState.asStateFlow() // อย่าลืมสร้างตัวแปร val ให้ UI อ่านค่าได้ด้วยนะครับ
    private val udf = UiStateHolder(_formState.value)
    val uiState: StateFlow<UiState<ShareAssetState<T>>> = udf.state
    val effects = udf.effects

    private val _friendState = MutableStateFlow<List<FriendData>>(emptyList())
    val friendState = _friendState.asStateFlow()

    private val _groupState = MutableStateFlow<List<GroupSummary>>(emptyList())
    val groupState = _groupState.asStateFlow()
    private var fetchJob: Job? = null



    fun initData(request: T?) {
        _formState.update { it.copy(request = request) }
        syncUdf()
    }
    fun saveShareInfo(shareTo: ShareTo) {
        _formState.update { it.copy(shareTo = shareTo) }
        syncUdf()
    }

    fun onAction(action: SummaryAction<T>) {
        when (action) {
            is SummaryAction.Changed -> initData(action.value)
            is SummaryAction.ShareChanged -> saveShareInfo(action.value)
            SummaryAction.Submit -> fetchData()
        }
    }

    private fun syncUdf() {
        udf.set(_formState.value)
    }



     fun fetchData() {
        if (fetchJob?.isActive == true) return
        _formState.update { it.copy(isLoading = true) }
        udf.loading()
        val job = screenModelScope.launch {
            try {
                val friendDeferred = async { friendRepository.getFriend() }
                val groupDeferred = async { groupRepository.getAllGroup() }
                val friendResult = friendDeferred.await()
                val groupResult = groupDeferred.await()
                var failure: com.wealthvault.core.architecture.AppError? = null
                friendResult.onSuccess { _friendState.value = it }
                    .onFailure { failure = it.toAppError() }
                groupResult.onSuccess { _groupState.value = it }
                    .onFailure { if (failure == null) failure = it.toAppError() }
                _formState.update { it.copy(isLoading = false) }
                if (failure == null) {
                    udf.success(_formState.value)
                    udf.emit(FormEffect.Saved)
                } else {
                    udf.failure(failure)
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                _formState.update { it.copy(isLoading = false) }
                udf.failure(error.toAppError())
            }
        }
        fetchJob = job
        job.invokeOnCompletion { if (fetchJob === job) fetchJob = null }
    }
}
