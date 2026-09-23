package com.wealthvault.financiallist.ui.asset.form.insurance

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.domain.portfolio.UpdateInsuranceRepository
import com.wealthvault.domain.portfolio.CreateInsuranceRepository
import com.wealthvault.domain.portfolio.InsuranceFileUploadData
import com.wealthvault.domain.portfolio.InsuranceRequest
import com.wealthvault.core.model.Attachment
import com.wealthvault.core.model.Money
import com.wealthvault.domain.portfolio.InsuranceModel
import com.wealthvault.core.architecture.FormAction
import com.wealthvault.core.architecture.FormEffect
import com.wealthvault.core.architecture.UiState
import com.wealthvault.core.architecture.UiStateHolder
import com.wealthvault.core.architecture.toAppError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.Job
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

class InsuranceScreenModel(
    private val insuranceRepository: UpdateInsuranceRepository,
    private val createInsuranceRepository: CreateInsuranceRepository,
) : ScreenModel {
    // 📦 ถังเก็บข้อมูล
    private val _state = MutableStateFlow(
        InsuranceModel(
            policyNumber = "",
            type = "",
            companyName = "",
            coverageAmount = Money(0),
            coveragePeriod = "",
            expDate = "",
            description = "",
            name = "",
            attachments = emptyList(),
            conDate = ""
        )
    )
    val state = _state.asStateFlow()
    private val udf = UiStateHolder(_state.value)
    val uiState: StateFlow<UiState<InsuranceModel>> = udf.state
    val effects = udf.effects
    private var submitJob: Job? = null
    private val _addedAttachments = MutableStateFlow<List<Attachment>>(emptyList())
    private val _deleteAttachments = MutableStateFlow<List<Attachment>>(emptyList())

    fun onAction(action: FormAction<InsuranceModel>) {
        when (action) {
            is FormAction.Changed -> updateForm(action.value)
            is FormAction.AttachmentsChanged -> updateAttachment(action.added, action.deleted)
            is FormAction.Submit -> submitInsurance(action.id)
        }
    }

 

    fun updateForm(data: InsuranceModel) {
        _state.update { it.copy(
            policyNumber = data.policyNumber,
            type = data.type,
            companyName = data.companyName,
            coverageAmount = data.coverageAmount,
            coveragePeriod = data.coveragePeriod,
            expDate = data.expDate,
            description = data.description,
            name = data.name,
            attachments = data.attachments,
            conDate = data.conDate
        ) }
        udf.set(_state.value, isLoading = false, error = null)
    }

    fun updateAttachment(addedList: List<Attachment>,deletedList: List<Attachment>) {
        _addedAttachments.update { addedList }
        _deleteAttachments.update { deletedList }


    }

    private fun asRequest(): InsuranceRequest {
        val current = _state.value



        // ✅ Map ข้อมูลให้มีทั้ง Byte, MimeType และ ชื่อไฟล์
        val allFiles = _addedAttachments.value.mapNotNull { attachment ->
            val bytes = attachment.platformData as? ByteArray ?: return@mapNotNull null

            // เช็กว่าเป็น PDF หรือ รูปภาพ
            val isPdf = attachment.name.endsWith(".pdf", ignoreCase = true) || attachment.type.toString().contains("PDF")
            val mimeType = if (isPdf) "application/pdf" else "image/jpeg"
            val extension = if (isPdf) "pdf" else "jpg"

            // ตั้งชื่อไฟล์ (เอา symbol มาต่อกับ index หรือเวลาเพื่อไม่ให้ซ้ำ)
            val fileName = "${current.name}.$extension"

            InsuranceFileUploadData(bytes = bytes, mimeType = mimeType, fileName = fileName)
        }

        return InsuranceRequest(
            name = current.name,
            policyNumber = current.policyNumber,
            type = current.type,
            companyName = current.companyName,
            coverageAmount = current.coverageAmount,
            coveragePeriod = current.coveragePeriod,
            conDate = current.conDate,
            expDate = current.expDate,
            description = current.description,
            files = allFiles,
            deleteListId = _deleteAttachments.value.map { it.id ?: "" } ,

        )
    }



    fun submitInsurance(id:String,onSuccess: () -> Unit = {}) {
        if (submitJob?.isActive == true) return
        udf.loading()
        val job = screenModelScope.launch {
            try {
//                isLoading = true
//                errorMessage = null

                // --- ขั้นตอนที่ 1: สร้าง insurance ก่อน ---

                val requestBody = asRequest()

                val insuranceResult = insuranceRepository.updateInsurance(id,requestBody)

                // ดึงข้อมูลออกมาจาก Result Wrapper
                val insuranceResponse = insuranceResult.getOrNull()

                if (insuranceResult.isSuccess && insuranceResponse != null) {
                    // ✅ ดึง ID ที่ได้จาก API ของการสร้าง insurance
                    // สมมติว่า field id อยู่ใน insuranceResponse.data.id หรือตาม Model ของคุณ
                    val createdItemId = insuranceResponse.id.toString()
                    udf.success()
                    udf.emit(FormEffect.Saved)
                    onSuccess()

                }
                else {
                    udf.failure(insuranceResult.exceptionOrNull()?.toAppError()
                        ?: IllegalStateException("Insurance update failed").toAppError())
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

    /** Creates a new insurance item and returns its domain id to the share step. */
    fun submitCreate(onSuccess: (String) -> Unit = {}) {
        if (submitJob?.isActive == true) return
        udf.loading()
        val job = screenModelScope.launch {
            try {
                when (val result = createInsuranceRepository.createInsurance(asRequest().copy(deleteListId = emptyList()))) {
                    is com.wealthvault.core.architecture.AppResult.Success -> {
                        val id = result.value.id?.takeIf { it.isNotBlank() }
                            ?: error("Insurance create response did not include an id")
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
