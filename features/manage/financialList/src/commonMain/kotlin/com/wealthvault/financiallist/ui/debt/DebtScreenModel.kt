package com.wealthvault.financiallist.ui.debt

// 🌟 Import Data Class
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.domain.social.ShareTargetsRepository
import com.wealthvault.financiallist.usecase.FinanciallistUseCase
import com.wealthvault.core.architecture.getOrNull
import com.wealthvault.core.architecture.onFailure
import com.wealthvault.core.architecture.onSuccess
import com.wealthvault.core.architecture.AppError
import com.wealthvault.core.architecture.UiAction
import com.wealthvault.core.architecture.UiEffect
import com.wealthvault.core.architecture.UiState
import com.wealthvault.core.architecture.toAppError
import com.wealthvault.domain.portfolio.GetLiabilityData
import com.wealthvault.domain.portfolio.LiabilityIdData
import com.wealthvault.domain.social.ShareTargets
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

data class DebtUiData(
    val loans: List<GetLiabilityData> = emptyList(),
    val expenses: List<GetLiabilityData> = emptyList(),
    val shareTargets: ShareTargets = ShareTargets(),
)

sealed interface DebtUiAction : UiAction {
    data object Refresh : DebtUiAction
    data class Delete(val id: String, val type: String) : DebtUiAction
    data class LoadShareTargets(val id: String, val type: String) : DebtUiAction
}

sealed interface DebtUiEffect : UiEffect {
    data object Deleted : DebtUiEffect
    data class ShowError(val error: AppError) : DebtUiEffect
}

class DebtScreenModel(
    private val useCase: FinanciallistUseCase,
    private val shareTargetsRepository: ShareTargetsRepository
) : ScreenModel {

    private val _uiState = MutableStateFlow<UiState<DebtUiData>>(
        UiState(data = DebtUiData(), isLoading = true),
    )
    val uiState: StateFlow<UiState<DebtUiData>> = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<DebtUiEffect>(extraBufferCapacity = 1)
    val effects = _effects.asSharedFlow()

    // 🌟 แยกเก็บ 2 หมวด
    private val _loans = MutableStateFlow<List<GetLiabilityData>>(emptyList())
    val loans = _loans.asStateFlow()

    private val _expenses = MutableStateFlow<List<GetLiabilityData>>(emptyList())
    val expenses = _expenses.asStateFlow()

    private val _shareTargets = MutableStateFlow<ShareTargets>(ShareTargets())
    val shareTargets = _shareTargets.asStateFlow()

    private var refreshJob: Job? = null
    private var mutationJob: Job? = null
    private var shareTargetJob: Job? = null

    fun onAction(action: DebtUiAction) {
        when (action) {
            DebtUiAction.Refresh -> fetchLiabilities(forceRefresh = true)
            is DebtUiAction.Delete -> deleteLiability(action.id, action.type)
            is DebtUiAction.LoadShareTargets -> getShareTarget(action.id, action.type)
        }
    }

    private fun syncUiState(
        isLoading: Boolean = _uiState.value.isLoading,
        error: AppError? = _uiState.value.error,
    ) {
        _uiState.value = UiState(
            data = DebtUiData(
                loans = _loans.value,
                expenses = _expenses.value,
                shareTargets = _shareTargets.value,
            ),
            isLoading = isLoading,
            error = error,
        )
    }

    fun fetchLiabilities(forceRefresh: Boolean = false) {
        if (refreshJob?.isActive == true) return
        val job = screenModelScope.launch {
            try {
                syncUiState(isLoading = true, error = null)
                useCase.getLiabilities(forceRefresh)
                    .onSuccess { allLiabilities ->
                        // 🌟 ดักฟังข้อมูลดิบที่ได้มาก่อนจะทำการ Filter

                        // 🌟 Filter ตาม Type
                        _loans.value = allLiabilities.filter { it.type == "LIABILITY_TYPE_LOAN" }
                        _expenses.value = allLiabilities.filter { it.type == "LIABILITY_TYPE_EXPENSE" }
                        syncUiState(isLoading = false, error = null)

                    }
                    .onFailure { error ->
                        val appError = error.toAppError()
                        syncUiState(isLoading = false, error = appError)
                        _effects.tryEmit(DebtUiEffect.ShowError(appError))
                    }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                val appError = error.toAppError()
                syncUiState(isLoading = false, error = appError)
                _effects.tryEmit(DebtUiEffect.ShowError(appError))
            }
        }
        refreshJob = job
        job.invokeOnCompletion { if (refreshJob === job) refreshJob = null }
    }
    suspend fun getLiabilityById(id: String): LiabilityIdData? {
        return useCase.getLiabilityById(id)
            .getOrNull()
    }
    fun deleteLiability(id: String, type: String) {
        if (mutationJob?.isActive == true || refreshJob?.isActive == true) return
        val job = screenModelScope.launch {
            try {
                syncUiState(isLoading = true, error = null)
                // useCase ตัวเดียวกับหน้า Asset ได้เลยถ้า inject มาถูกตัว
                val result = useCase.deleteAsset(id, type)

                result.onSuccess {
                    _effects.tryEmit(DebtUiEffect.Deleted)
                    fetchLiabilities(forceRefresh = true) // 🌟 พอลบเสร็จ สั่งโหลดข้อมูลหน้านี้ใหม่ทันที
                }.onFailure { error ->
                    val appError = error.toAppError()
                    syncUiState(isLoading = false, error = appError)
                    _effects.tryEmit(DebtUiEffect.ShowError(appError))
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                val appError = error.toAppError()
                syncUiState(isLoading = false, error = appError)
                _effects.tryEmit(DebtUiEffect.ShowError(appError))
            }
        }
        mutationJob = job
        job.invokeOnCompletion {
            if (mutationJob === job) mutationJob = null
        }
    }

    fun getShareTarget(id:String,type:String) {
        if (shareTargetJob?.isActive == true) return
        val job = screenModelScope.launch {
            try {
                val shareTargetsResult = shareTargetsRepository.shareTargets(id,type)
                shareTargetsResult.onSuccess { data ->
                    _shareTargets.value = data
                    syncUiState(error = null)

                }.onFailure { error ->
                    val appError = error.toAppError()
                    syncUiState(error = appError)
                    _effects.tryEmit(DebtUiEffect.ShowError(appError))
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                val appError = error.toAppError()
                syncUiState(error = appError)
                _effects.tryEmit(DebtUiEffect.ShowError(appError))
            }

        }
        shareTargetJob = job
        job.invokeOnCompletion {
            if (shareTargetJob === job) shareTargetJob = null
        }
    }
}
