package com.wealthvault.financiallist.ui.asset.form.building

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.domain.portfolio.BuildingFileUploadData
import com.wealthvault.domain.portfolio.BuildingReferenceData
import com.wealthvault.domain.portfolio.BuildingRequest
import com.wealthvault.domain.portfolio.InsReferenceData
import com.wealthvault.domain.portfolio.UpdateBuildingRepository
import com.wealthvault.domain.portfolio.CreateBuildingRepository
import com.wealthvault.domain.portfolio.GetInsuranceData
import com.wealthvault.domain.portfolio.GetLandData
import com.wealthvault.core.model.Attachment
import com.wealthvault.core.model.Money
import com.wealthvault.domain.portfolio.InsuranceReferenceRepository
import com.wealthvault.domain.portfolio.LandReferenceRepository
import com.wealthvault.domain.portfolio.BuildingModel
import com.wealthvault.domain.portfolio.InsRefModel
import com.wealthvault.domain.portfolio.RefModel
import com.wealthvault.core.architecture.FormAction
import com.wealthvault.core.architecture.FormEffect
import com.wealthvault.core.architecture.UiState
import com.wealthvault.core.architecture.UiStateHolder
import com.wealthvault.core.architecture.toAppError
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class BuildingScreenModel(
    private val landRepository: LandReferenceRepository,
    private val insuranceRepository: InsuranceReferenceRepository,
    private val buildingRepository: UpdateBuildingRepository,
    private val createBuildingRepository: CreateBuildingRepository,
) : ScreenModel {

    private val _LandState = MutableStateFlow<List<GetLandData>>(emptyList())
    val LandState = _LandState.asStateFlow()

    private val _InsState = MutableStateFlow<List<GetInsuranceData>>(emptyList())
    val InsState = _InsState.asStateFlow()





    private val _state = MutableStateFlow(

        BuildingModel(
            type = "",
            buildingName = "",
            area = 0.0,
            amount = Money(0),
            description = "",
            attachments = emptyList(),
            referenceIds =  emptyList(),
            locationAddress = "",
            locationSubDistrict = "",
            locationDistrict = "",
            locationProvince = "",
            locationPostalCode = "",
            insIds = emptyList(),

        )
    )
    val state = _state.asStateFlow()

    private val udf = UiStateHolder(_state.value)
    val uiState: StateFlow<UiState<BuildingModel>> = udf.state
    val effects = udf.effects
    private var submitJob: Job? = null
    private var fetchJob: Job? = null

    fun onAction(action: FormAction<BuildingModel>) {
        when (action) {
            is FormAction.Changed -> updateForm(action.value)
            is FormAction.AttachmentsChanged -> updateAttachment(
                action.added,
                action.deleted,
                emptyList(),
                emptyList(),
                emptyList(),
                emptyList(),
            )
            is FormAction.Submit -> submitLand(action.id)
        }
    }

    // ✍️ ฟังก์ชันอัปเดตข้อมูลจากหน้าฟอร์ม
    fun updateForm(data: BuildingModel) {
        _state.update { it.copy(
            type = data.type,
            buildingName = data.buildingName,
            area = data.area,
            amount = data.amount,
            description = data.description,
            attachments = data.attachments,
            referenceIds = data.referenceIds,
            locationAddress = data.locationAddress,
            locationSubDistrict = data.locationSubDistrict,
            locationDistrict = data.locationDistrict,
            locationProvince = data.locationProvince,
            locationPostalCode = data.locationPostalCode,
            insIds = data.insIds,
        ) }
        udf.set(_state.value, isLoading = false, error = null)
    }



    fun fetchData() {
        if (fetchJob?.isActive == true) return
        udf.loading()
        val job = screenModelScope.launch {
            try {
                val landDeferred = async { landRepository.getLand() }
                val insuranceDeferred = async { insuranceRepository.getInsurance() }
                val landResult = landDeferred.await()
                val insuranceResult = insuranceDeferred.await()
                var failure: com.wealthvault.core.architecture.AppError? = null

                when (landResult) {
                    is com.wealthvault.core.architecture.AppResult.Success -> _LandState.value = landResult.value
                    is com.wealthvault.core.architecture.AppResult.Failure -> failure = landResult.error
                }
                when (insuranceResult) {
                    is com.wealthvault.core.architecture.AppResult.Success -> _InsState.value = insuranceResult.value
                    is com.wealthvault.core.architecture.AppResult.Failure -> {
                        if (failure == null) failure = insuranceResult.error
                    }
                }
                failure?.let(udf::failure) ?: udf.success()
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                udf.failure(error.toAppError())
            }
        }
        fetchJob = job
        job.invokeOnCompletion { if (fetchJob === job) fetchJob = null }
    }


    private val _addedAttachments = MutableStateFlow<List<Attachment>>(emptyList())
    private val _deleteAttachments = MutableStateFlow<List<Attachment>>(emptyList())

    private val _addedRef = MutableStateFlow<List<RefModel>>(emptyList())
    private val _deleteRef = MutableStateFlow<List<RefModel>>(emptyList())

    private val _addedIns = MutableStateFlow<List<InsRefModel>>(emptyList())
    private val _deleteIns = MutableStateFlow<List<InsRefModel>>(emptyList())






    fun updateAttachment(addedList: List<Attachment>, deletedList: List<Attachment>, addedRef:List<RefModel>, deletedRef:List<RefModel>, addedIns:List<InsRefModel>, deletedIns:List<InsRefModel>) {
        _addedAttachments.update { addedList }
        _deleteAttachments.update { deletedList }
        _addedRef.update { addedRef }
        _deleteRef.update { deletedRef }
        _addedIns.update { addedIns }
        _deleteIns.update { deletedIns }
    }

    private fun asRequest(): BuildingRequest = buildBuildingRequest(
        current = _state.value,
        addedAttachments = _addedAttachments.value,
        deletedAttachments = _deleteAttachments.value,
        addedReferences = _addedRef.value,
        deletedReferences = _deleteRef.value,
        addedInsurance = _addedIns.value,
        deletedInsurance = _deleteIns.value
    )



    fun submitLand(id:String,onSuccess: () -> Unit = {}) {
        if (submitJob?.isActive == true) return
        udf.loading()
        val job = screenModelScope.launch {
            try {
//                isLoading = true
//                errorMessage = null

                // --- ขั้นตอนที่ 1: สร้าง Land ก่อน ---

                val requestBody = asRequest()

                val buildingResult = buildingRepository.updateBuilding(id,requestBody)

                // ดึงข้อมูลออกมาจาก Result Wrapper
                val buildingResponse = buildingResult.getOrNull()

                if (buildingResult.isSuccess && buildingResponse != null) {
                    // ✅ ดึง ID ที่ได้จาก API ของการสร้าง Land
                    // สมมติว่า field id อยู่ใน landResponse.data.id หรือตาม Model ของคุณ
                    val createdItemId = buildingResponse.id
                    udf.success()
                    udf.emit(FormEffect.Saved)
                    onSuccess()

                }
                else {
                    udf.failure(buildingResult.exceptionOrNull()?.toAppError()
                        ?: IllegalStateException("Building update failed").toAppError())
                }

            } catch (e: CancellationException) {
                throw e
            } catch (e: Throwable) {
                udf.failure(e.toAppError())
            } finally {
//                isLoading = false
            }
        }
        submitJob = job
        job.invokeOnCompletion { if (submitJob === job) submitJob = null }
    }

    /** Creates a new building and returns its domain id to the share step. */
    fun submitCreate(onSuccess: (String) -> Unit = {}) {
        if (submitJob?.isActive == true) return
        udf.loading()
        val job = screenModelScope.launch {
            try {
                when (val result = createBuildingRepository.createBuilding(
                    asRequest().copy(
                        deleteListId = emptyList(),
                        deleteRefListId = emptyList(),
                        deleteInsListId = emptyList(),
                    ),
                )) {
                    is com.wealthvault.core.architecture.AppResult.Success -> {
                        val id = result.value.id?.takeIf { it.isNotBlank() }
                            ?: error("Building create response did not include an id")
                        udf.success()
                        udf.emit(FormEffect.Saved)
                        onSuccess(id)
                    }
                    is com.wealthvault.core.architecture.AppResult.Failure -> udf.failure(result.error)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Throwable) {
                udf.failure(e.toAppError())
            }
        }
        submitJob = job
        job.invokeOnCompletion { if (submitJob === job) submitJob = null }
    }


}
