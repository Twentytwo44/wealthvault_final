package com.wealthvault.financiallist.ui.asset.form.land

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.domain.portfolio.GetBuildingData
import com.wealthvault.domain.portfolio.UpdateLandRepository
import com.wealthvault.domain.portfolio.CreateLandRepository
import com.wealthvault.domain.portfolio.LandFileUploadData
import com.wealthvault.domain.portfolio.LandReferenceData
import com.wealthvault.domain.portfolio.LandRequest
import com.wealthvault.core.model.Attachment
import com.wealthvault.core.model.Money
import com.wealthvault.domain.portfolio.BuildingReferenceRepository
import com.wealthvault.domain.portfolio.LandModel
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

class LandScreenModel(
    private val buildingRepository: BuildingReferenceRepository,
    private val landRepository: UpdateLandRepository,
    private val createLandRepository: CreateLandRepository,
) : ScreenModel {
    // 📦 ถังเก็บข้อมูล
    private val _state = MutableStateFlow(

        LandModel(
            deedNum = "",
            landName = "",
            area = 0.0,
            amount = Money(0),
            description = "",
            attachments = emptyList(),
            referenceIds = emptyList(),
            locationAddress = "",
            locationSubDistrict = "",
            locationDistrict = "",
            locationProvince = "",
            locationPostalCode = "",

        )
    )
    val state = _state.asStateFlow()

    private val udf = UiStateHolder(_state.value)
    val uiState: StateFlow<UiState<LandModel>> = udf.state
    val effects = udf.effects
    private var submitJob: Job? = null
    private var fetchJob: Job? = null

    fun onAction(action: FormAction<LandModel>) {
        when (action) {
            is FormAction.Changed -> updateForm(action.value)
            is FormAction.AttachmentsChanged -> updateAttachment(
                action.added,
                action.deleted,
                emptyList(),
                emptyList(),
            )
            is FormAction.Submit -> submitLand(action.id)
        }
    }

    private val _BuildingState = MutableStateFlow<List<GetBuildingData>>(emptyList())
    val BuildingState = _BuildingState.asStateFlow()




    // ✍️ ฟังก์ชันอัปเดตข้อมูลจากหน้าฟอร์ม
    fun updateForm(data: LandModel) {
        _state.update { it.copy(
            deedNum = data.deedNum,
            landName = data.landName,
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
        ) }
        udf.set(_state.value, isLoading = false, error = null)
    }

    fun fetchData() {
        if (fetchJob?.isActive == true) return
        udf.loading()
        val job = screenModelScope.launch {
            try {
                val buildingResult = async { buildingRepository.getBuilding() }.await()
                when (buildingResult) {
                    is com.wealthvault.core.architecture.AppResult.Success -> {
                        _BuildingState.value = buildingResult.value
                        udf.success()
                    }
                    is com.wealthvault.core.architecture.AppResult.Failure -> {
                        udf.failure(buildingResult.error)
                    }
                }
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





    fun updateAttachment(addedList: List<Attachment>,deletedList: List<Attachment>,addedRef:List<RefModel>,deletedRef:List<RefModel>) {
        _addedAttachments.update { addedList }
        _deleteAttachments.update { deletedList }
        _addedRef.update { addedRef }
        _deleteRef.update { deletedRef }
    }

    private fun asRequest(): LandRequest {
        val current = _state.value



        // ✅ Map ข้อมูลให้มีทั้ง Byte, MimeType และ ชื่อไฟล์
        val allFiles = _addedAttachments.value.mapNotNull { attachment ->
            val bytes = attachment.platformData as? ByteArray ?: return@mapNotNull null

            // เช็กว่าเป็น PDF หรือ รูปภาพ
            val isPdf = attachment.name.endsWith(".pdf", ignoreCase = true) || attachment.type.toString().contains("PDF")
            val mimeType = if (isPdf) "application/pdf" else "image/jpeg"
            val extension = if (isPdf) "pdf" else "jpg"

            // ตั้งชื่อไฟล์ (เอา symbol มาต่อกับ index หรือเวลาเพื่อไม่ให้ซ้ำ)
            val fileName = "${current.landName}.$extension"

            LandFileUploadData(bytes = bytes, mimeType = mimeType, fileName = fileName)
        }

        return LandRequest(
            name = current.landName,
            deedNum = current.deedNum,
            area = current.area,
            amount = current.amount,
            description = current.description,
            locationAddress = current.locationAddress,
            locationSubDistrict = current.locationSubDistrict,
            locationDistrict = current.locationDistrict,
            locationProvince = current.locationProvince,
            locationPostalCode = current.locationPostalCode,
            files = allFiles,
            referenceIds = _addedRef.value.map { data ->
                LandReferenceData(
                    areaName = data.areaName,
                    areaId = data.areaId
                ) },
            deleteRefListId = _deleteRef.value.map {data ->
                LandReferenceData(
                    areaName = data.areaName,
                    areaId = data.areaId
                )
            },
            deleteListId = _deleteAttachments.value.map { it.id ?: "" }
        )
    }



    fun submitLand(id:String,onSuccess: () -> Unit = {}) {
        if (submitJob?.isActive == true) return
        udf.loading()
        val job = screenModelScope.launch {
            try {
//                isLoading = true
//                errorMessage = null

                // --- ขั้นตอนที่ 1: สร้าง Land ก่อน ---

                val requestBody = asRequest()

                val landResult = landRepository.updateLand(id,requestBody)
                // ดึงข้อมูลออกมาจาก Result Wrapper
                val landResponse = landResult.getOrNull()

                if (landResult.isSuccess && landResponse != null) {
                    // ✅ ดึง ID ที่ได้จาก API ของการสร้าง Land
                    // สมมติว่า field id อยู่ใน landResponse.data.id หรือตาม Model ของคุณ
                    val createdItemId = landResponse.id.toString()
                    udf.success()
                    udf.emit(FormEffect.Saved)
                    onSuccess()

                }
                else {
                    udf.failure(landResult.exceptionOrNull()?.toAppError()
                        ?: IllegalStateException("Land update failed").toAppError())
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

    /** Creates a new land asset and returns its domain id to the share step. */
    fun submitCreate(onSuccess: (String) -> Unit = {}) {
        if (submitJob?.isActive == true) return
        udf.loading()
        val job = screenModelScope.launch {
            try {
                when (val result = createLandRepository.create(
                    asRequest().copy(deleteListId = emptyList(), deleteRefListId = emptyList()),
                )) {
                    is com.wealthvault.core.architecture.AppResult.Success -> {
                        val id = result.value.id?.takeIf { it.isNotBlank() }
                            ?: error("Land create response did not include an id")
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
