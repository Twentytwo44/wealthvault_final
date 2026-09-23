package com.wealthvault.financiallist.ui.asset.form.cash

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.domain.portfolio.CashFileUploadData
import com.wealthvault.domain.portfolio.CashRequest
import com.wealthvault.domain.portfolio.UpdateCashRepository
import com.wealthvault.domain.portfolio.CreateCashRepository
import com.wealthvault.core.model.Attachment
import com.wealthvault.core.model.Money
import com.wealthvault.domain.portfolio.CashModel
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

class CashScreenModel(
    private val cashRepository: UpdateCashRepository,
    private val createCashRepository: CreateCashRepository,
) : ScreenModel {
    // 📦 ถังเก็บข้อมูล

    private val _cashData = MutableStateFlow(CashModel(
        cashName = "",
        amount = Money(0),
        description = "",
        attachments = emptyList()
    ))
    val state = _cashData.asStateFlow()
    private val udf = UiStateHolder(_cashData.value)
    val uiState: StateFlow<UiState<CashModel>> = udf.state
    val effects = udf.effects
    private var submitJob: Job? = null
    private val _addedAttachments = MutableStateFlow<List<Attachment>>(emptyList())
    private val _deleteAttachments = MutableStateFlow<List<Attachment>>(emptyList())

    fun onAction(action: FormAction<CashModel>) {
        when (action) {
            is FormAction.Changed -> updateForm(action.value)
            is FormAction.AttachmentsChanged -> updateAttachment(action.added, action.deleted)
            is FormAction.Submit -> submitCash(action.id)
        }
    }


    fun updateForm(data: CashModel) {
        _cashData.update { it.copy(cashName = data.cashName, amount = data.amount, description = data.description, attachments = data.attachments) }
        udf.set(_cashData.value, isLoading = false, error = null)
    }

    fun updateAttachment(addedList: List<Attachment>,deletedList: List<Attachment>) {
        _addedAttachments.update { addedList }
        _deleteAttachments.update { deletedList }
    }

    private fun asRequest(): CashRequest {
        val current = _cashData.value



        // ✅ Map ข้อมูลให้มีทั้ง Byte, MimeType และ ชื่อไฟล์
        val allFiles = _addedAttachments.value.mapNotNull { attachment ->
            val bytes = attachment.platformData as? ByteArray ?: return@mapNotNull null

            // เช็กว่าเป็น PDF หรือ รูปภาพ
            val isPdf = attachment.name.endsWith(".pdf", ignoreCase = true) || attachment.type.toString().contains("PDF")
            val mimeType = if (isPdf) "application/pdf" else "image/jpeg"
            val extension = if (isPdf) "pdf" else "jpg"

            // ตั้งชื่อไฟล์ (เอา symbol มาต่อกับ index หรือเวลาเพื่อไม่ให้ซ้ำ)
            val fileName = "${current.cashName}.$extension"

            CashFileUploadData(bytes = bytes, mimeType = mimeType, fileName = fileName)
        }

        return CashRequest(
            name = current.cashName,
            amount = current.amount,
            description = current.description,
            files = allFiles,
            deleteListId = _deleteAttachments.value.map { it.id ?: "" }
        )
    }



    fun submitCash(id:String,onSuccess: () -> Unit = {}) {
        if (submitJob?.isActive == true) return
        udf.loading()
        val job = screenModelScope.launch {
            try {
//                isLoading = true
//                errorMessage = null

                // --- ขั้นตอนที่ 1: สร้าง Cash ก่อน ---

                val requestBody = asRequest()

                val cashResult = cashRepository.updateCash(id,requestBody)

                // ดึงข้อมูลออกมาจาก Result Wrapper
                val cashResponse = cashResult.getOrNull()

                if (cashResult.isSuccess && cashResponse != null) {
                    // ✅ ดึง ID ที่ได้จาก API ของการสร้าง Cash
                    // สมมติว่า field id อยู่ใน cashResponse.data.id หรือตาม Model ของคุณ
                    val createdItemId = cashResponse.id.toString()
                    udf.success()
                    udf.emit(FormEffect.Saved)
                    onSuccess()

                }
                else {
                    udf.failure(cashResult.exceptionOrNull()?.toAppError()
                        ?: IllegalStateException("Cash update failed").toAppError())
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

    /** Creates a new cash asset and returns its domain id to the share step. */
    fun submitCreate(onSuccess: (String) -> Unit = {}) {
        if (submitJob?.isActive == true) return
        udf.loading()
        val job = screenModelScope.launch {
            try {
                when (val result = createCashRepository.create(asRequest().copy(deleteListId = emptyList()))) {
                    is com.wealthvault.core.architecture.AppResult.Success -> {
                        val id = result.value.id?.takeIf { it.isNotBlank() }
                            ?: error("Cash create response did not include an id")
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
