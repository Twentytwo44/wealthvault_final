package com.wealthvault.financiallist.ui.asset

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.core.observability.AppLogger
import com.wealthvault.core.observability.NoOpAppLogger
import com.wealthvault.core.architecture.AppError
import com.wealthvault.core.architecture.UiAction
import com.wealthvault.core.architecture.UiEffect
import com.wealthvault.core.architecture.UiState
import com.wealthvault.core.architecture.getOrNull
import com.wealthvault.core.architecture.onFailure
import com.wealthvault.core.architecture.onSuccess
import com.wealthvault.core.architecture.toAppError
import com.wealthvault.domain.portfolio.AccountData
import com.wealthvault.domain.portfolio.BankAccountData
import com.wealthvault.domain.portfolio.BuildingIdData
import com.wealthvault.domain.portfolio.GetBuildingData
import com.wealthvault.domain.portfolio.CashIdData
import com.wealthvault.domain.portfolio.GetCashData
import com.wealthvault.domain.social.ShareTargetsRepository
import com.wealthvault.financiallist.usecase.FinanciallistUseCase
import com.wealthvault.domain.portfolio.GetInsuranceData
import com.wealthvault.domain.portfolio.InsuranceIdData
import com.wealthvault.domain.portfolio.GetInvestmentData
import com.wealthvault.domain.portfolio.InvestmentIdData
import com.wealthvault.domain.portfolio.GetLandData
import com.wealthvault.domain.portfolio.LandIdData
import com.wealthvault.domain.social.ShareTargets
import kotlinx.coroutines.async
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

data class AssetUiData(
    val accounts: List<AccountData> = emptyList(),
    val cashes: List<GetCashData> = emptyList(),
    val investments: List<GetInvestmentData> = emptyList(),
    val insurances: List<GetInsuranceData> = emptyList(),
    val buildings: List<GetBuildingData> = emptyList(),
    val lands: List<GetLandData> = emptyList(),
    val shareTargets: ShareTargets = ShareTargets(),
)

sealed interface AssetUiAction : UiAction {
    data object Refresh : AssetUiAction
    data class Delete(val id: String, val type: String) : AssetUiAction
    data class LoadShareTargets(val id: String, val type: String) : AssetUiAction
}

sealed interface AssetUiEffect : UiEffect {
    data object Deleted : AssetUiEffect
    data class ShowError(val error: AppError) : AssetUiEffect
}

class AssetScreenModel(
    private val useCase: FinanciallistUseCase,
    private val shareTargetsRepository: ShareTargetsRepository,
    private val logger: AppLogger = NoOpAppLogger,
) : ScreenModel {

    private val _uiState = MutableStateFlow<UiState<AssetUiData>>(
        UiState(data = AssetUiData(), isLoading = true),
    )
    val uiState: StateFlow<UiState<AssetUiData>> = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<AssetUiEffect>(extraBufferCapacity = 1)
    val effects = _effects.asSharedFlow()

    private val _accounts = MutableStateFlow<List<AccountData>>(emptyList())
    val accounts = _accounts.asStateFlow()

    private val _cashes = MutableStateFlow<List<GetCashData>>(emptyList())
    val cashes = _cashes.asStateFlow()

    private val _investments = MutableStateFlow<List<GetInvestmentData>>(emptyList())
    val investments = _investments.asStateFlow()

    private val _insurances = MutableStateFlow<List<GetInsuranceData>>(emptyList())
    val insurances = _insurances.asStateFlow()

    private val _buildings = MutableStateFlow<List<GetBuildingData>>(emptyList())
    val buildings = _buildings.asStateFlow()

    private val _lands = MutableStateFlow<List<GetLandData>>(emptyList())
    val lands = _lands.asStateFlow()

    private val _shareTargets = MutableStateFlow<ShareTargets>(ShareTargets())
    val shareTargets = _shareTargets.asStateFlow()

    private var refreshJob: Job? = null
    private var mutationJob: Job? = null
    private var shareTargetJob: Job? = null

    fun onAction(action: AssetUiAction) {
        when (action) {
            AssetUiAction.Refresh -> fetchAllAssets(forceRefresh = true)
            is AssetUiAction.Delete -> deleteAsset(action.id, action.type)
            is AssetUiAction.LoadShareTargets -> getShareTarget(action.id, action.type)
        }
    }

    private fun syncUiState(
        isLoading: Boolean = _uiState.value.isLoading,
        error: AppError? = _uiState.value.error,
    ) {
        _uiState.value = UiState(
            data = AssetUiData(
                accounts = _accounts.value,
                cashes = _cashes.value,
                investments = _investments.value,
                insurances = _insurances.value,
                buildings = _buildings.value,
                lands = _lands.value,
                shareTargets = _shareTargets.value,
            ),
            isLoading = isLoading,
            error = error,
        )
    }

    fun fetchAllAssets(forceRefresh: Boolean = false) {
        if (refreshJob?.isActive == true) return
        // Load all categories in parallel, but keep one cancellable job so a
        // recomposition/resume cannot fan out duplicate requests.
        val job = screenModelScope.launch {
            try {
                syncUiState(isLoading = true, error = null)
                var firstError: AppError? = null
                coroutineScope {
                    val accounts = async { useCase.getAccounts(forceRefresh) }
                    val cashes = async { useCase.getCashes(forceRefresh) }
                    val investments = async { useCase.getInvestments(forceRefresh) }
                    val insurances = async { useCase.getInsurances(forceRefresh) }
                    val buildings = async { useCase.getBuildings(forceRefresh) }
                    val lands = async { useCase.getLands(forceRefresh) }

                    accounts.await()
                        .onSuccess { _accounts.value = it }
                        .onFailure {
                            firstError = firstError ?: it.toAppError()
                            logger.warn("Loading accounts failed", it)
                        }
                    cashes.await()
                        .onSuccess { _cashes.value = it }
                        .onFailure {
                            firstError = firstError ?: it.toAppError()
                            logger.warn("Loading cash assets failed", it)
                        }
                    investments.await()
                        .onSuccess { _investments.value = it }
                        .onFailure {
                            firstError = firstError ?: it.toAppError()
                            logger.warn("Loading investments failed", it)
                        }
                    insurances.await()
                        .onSuccess { _insurances.value = it }
                        .onFailure {
                            firstError = firstError ?: it.toAppError()
                            logger.warn("Loading insurance assets failed", it)
                        }
                    buildings.await()
                        .onSuccess { _buildings.value = it }
                        .onFailure {
                            firstError = firstError ?: it.toAppError()
                            logger.warn("Loading buildings failed", it)
                        }
                    lands.await()
                        .onSuccess { _lands.value = it }
                        .onFailure {
                            firstError = firstError ?: it.toAppError()
                            logger.warn("Loading land assets failed", it)
                        }
                }
                syncUiState(isLoading = false, error = firstError)
                firstError?.let { _effects.tryEmit(AssetUiEffect.ShowError(it)) }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                val appError = error.toAppError()
                logger.warn("Loading assets failed unexpectedly", error)
                syncUiState(isLoading = false, error = appError)
                _effects.tryEmit(AssetUiEffect.ShowError(appError))
            }
        }
        refreshJob = job
        job.invokeOnCompletion { if (refreshJob === job) refreshJob = null }
    }

    // 🌟 เปลี่ยนมาเรียกใช้ useCase แทน repository
    suspend fun getAccountById(id: String): BankAccountData? {
        return useCase.getAccountById(id) // ⚠️ อย่าลืมไปเพิ่มฟังก์ชันนี้ใน FinanciallistUseCase ด้วยนะครับ
            .onFailure { logger.warn("Loading account details failed", it) }
            .getOrNull()
    }
    suspend fun getBuildingById(id: String): BuildingIdData? {
        return useCase.getBuildingById(id)
            .onFailure { logger.warn("Loading building details failed", it) }
            .getOrNull()
    }
    suspend fun getCashById(id: String): CashIdData? {
        return useCase.getCashById(id)
            .onFailure { logger.warn("Loading cash details failed", it) }
            .getOrNull()
    }
    suspend fun getInsuranceById(id: String): InsuranceIdData? {
        return useCase.getInsuranceById(id).getOrNull()
    }

    suspend fun getInvestmentById(id: String): InvestmentIdData? {
        return useCase.getInvestmentById(id).getOrNull()
    }

    suspend fun getLandById(id: String): LandIdData? {
        return useCase.getLandById(id).getOrNull()
    }
    // ในไฟล์ AssetScreenModel.kt

    fun deleteAsset(id: String, type: String) {
        if (mutationJob?.isActive == true || refreshJob?.isActive == true) return
        val job = screenModelScope.launch {
            try {
                syncUiState(isLoading = true, error = null)
                // 1. สั่งลบผ่าน UseCase
                val result = useCase.deleteAsset(id, type)

                result.onSuccess {
                    logger.info("Asset deletion succeeded")

                    // 🌟 2. จุดสำคัญ: ต้องเรียกฟังก์ชันนี้เพื่อให้มันไปดึงข้อมูลใหม่จาก API มาใส่ StateFlow
                    _effects.tryEmit(AssetUiEffect.Deleted)
                    fetchAllAssets(forceRefresh = true)

                }.onFailure { error ->
                    logger.warn("Asset deletion failed", error)
                    val appError = error.toAppError()
                    syncUiState(isLoading = false, error = appError)
                    _effects.tryEmit(AssetUiEffect.ShowError(appError))
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                val appError = error.toAppError()
                logger.warn("Asset deletion failed unexpectedly", error)
                syncUiState(isLoading = false, error = appError)
                _effects.tryEmit(AssetUiEffect.ShowError(appError))
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
                val shareTargetsResult = shareTargetsRepository.shareTargets(id, type)
                shareTargetsResult.onSuccess { data ->
                    _shareTargets.value = data
                    logger.debug("Share target list loaded")
                    syncUiState(error = null)

                }.onFailure { error ->
                    logger.warn("Share target list failed", error)
                    val appError = error.toAppError()
                    syncUiState(error = appError)
                    _effects.tryEmit(AssetUiEffect.ShowError(appError))
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                val appError = error.toAppError()
                logger.warn("Share target list failed unexpectedly", error)
                syncUiState(error = appError)
                _effects.tryEmit(AssetUiEffect.ShowError(appError))
            }

        }
        shareTargetJob = job
        job.invokeOnCompletion {
            if (shareTargetJob === job) shareTargetJob = null
        }
    }

}
