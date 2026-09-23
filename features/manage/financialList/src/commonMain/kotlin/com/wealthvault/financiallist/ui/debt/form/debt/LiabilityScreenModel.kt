package com.wealthvault.financiallist.ui.debt.form.debt

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.domain.portfolio.CreateLiabilityRepository
import com.wealthvault.domain.portfolio.UpdateLiabilityRepository
import com.wealthvault.domain.portfolio.LiabilityRequest
import com.wealthvault.domain.portfolio.LiabilityUploadData
import com.wealthvault.core.model.Attachment
import com.wealthvault.core.model.FixedDecimal
import com.wealthvault.core.model.Money
import com.wealthvault.domain.portfolio.LiabilityModel
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

class LiabilityScreenModel(
    private val liabilityRepository: UpdateLiabilityRepository,
    private val createLiabilityRepository: CreateLiabilityRepository,
) : ScreenModel {
    // 📦 ถังเก็บข้อมูล
    private val _state = MutableStateFlow(
        LiabilityModel(
            type = "",
            name = "",
            principal = Money(0),
            interestRate = "",
            description = "",
            startedAt = "",
            endedAt = "",
            creditor = "",
            attachments = emptyList()

        )
    )

    val state = _state.asStateFlow()
    private val udf = UiStateHolder(_state.value)
    val uiState: StateFlow<UiState<LiabilityModel>> = udf.state
    val effects = udf.effects
    private var submitJob: Job? = null

    fun onAction(action: FormAction<LiabilityModel>) {
        when (action) {
            is FormAction.Changed -> updateForm(action.value)
            is FormAction.AttachmentsChanged -> updateAttachment(action.added, action.deleted)
            is FormAction.Submit -> submitLiability(action.id)
        }
    }

    // ✍️ ฟังก์ชันอัปเดตข้อมูลจากหน้าฟอร์ม
    fun updateForm(data: LiabilityModel) {
        _state.update { it.copy(
            name = data.name,
            type = data.type,
            principal = data.principal,
            interestRate = data.interestRate,
            description = data.description,
            startedAt = data.startedAt.take(10),
            endedAt = data.endedAt.take(10),
            creditor = data.creditor,
            attachments = data.attachments

        ) }
        udf.set(_state.value, isLoading = false, error = null)
    }


    private val _addedAttachments = MutableStateFlow<List<Attachment>>(emptyList())
    private val _deleteAttachments = MutableStateFlow<List<Attachment>>(emptyList())

    fun updateAttachment(addedList: List<Attachment>,deletedList: List<Attachment>) {
        _addedAttachments.update { addedList }
        _deleteAttachments.update { deletedList }
    }

    private fun asRequest(type: String = "LIABILITY_TYPE_LOAN"): LiabilityRequest {
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

            LiabilityUploadData(bytes = bytes, mimeType = mimeType, fileName = fileName)
        }

        return LiabilityRequest(
            name = current.name,
            type = type,
            principal = current.principal,
            interestRate = FixedDecimal.fromDecimal(current.interestRate, scale = 4),
            description = current.description,
            startedAt = current.startedAt,
            endedAt = current.endedAt,
            creditor = current.creditor,
            files = allFiles,
            deleteListId = _deleteAttachments.value.map { it.id ?: "" }
        )
    }

    fun submitCreate(onSuccess: (String) -> Unit = {}) {
        if (submitJob?.isActive == true) return
        udf.loading()
        val job = screenModelScope.launch {
            try {
                val result = createLiabilityRepository.createLiability(asRequest())
                val createdId = result.getOrNull()?.id?.takeIf { it.isNotBlank() }
                if (result.isSuccess && createdId != null) {
                    udf.success()
                    udf.emit(FormEffect.Saved)
                    onSuccess(createdId)
                } else {
                    udf.failure(result.exceptionOrNull()?.toAppError()
                        ?: IllegalStateException("Liability create failed").toAppError())
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



    fun submitLiability(id:String,onSuccess: () -> Unit = {}) {
        if (submitJob?.isActive == true) return
        udf.loading()
        val job = screenModelScope.launch {
            try {
//                isLoading = true
//                errorMessage = null

                // --- ขั้นตอนที่ 1: สร้าง Liability ก่อน ---

                val requestBody = asRequest()

                val liabilityResult = liabilityRepository.updateLiability(id,requestBody)


                // ดึงข้อมูลออกมาจาก Result Wrapper
                val liabilityResponse = liabilityResult.getOrNull()

                if (liabilityResult.isSuccess && liabilityResponse != null) {
                    // ✅ ดึง ID ที่ได้จาก API ของการสร้าง Liability
                    // สมมติว่า field id อยู่ใน liabilityResponse.data.id หรือตาม Model ของคุณ
                    val createdItemId = liabilityResponse.id.toString()
                    udf.success()
                    udf.emit(FormEffect.Saved)
                    onSuccess()

                }
                else {
                    udf.failure(liabilityResult.exceptionOrNull()?.toAppError()
                        ?: IllegalStateException("Liability update failed").toAppError())
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

}
