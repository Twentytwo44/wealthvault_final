package com.wealthvault.financiallist.ui.asset.form.investment

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.domain.portfolio.UpdateInvestmentRepository
import com.wealthvault.domain.portfolio.CreateInvestmentRepository
import com.wealthvault.domain.portfolio.FileUploadData
import com.wealthvault.domain.portfolio.InvestmentRequest
import com.wealthvault.core.model.Attachment
import com.wealthvault.core.model.FixedDecimal
import com.wealthvault.core.model.Money
import com.wealthvault.domain.portfolio.StockModel
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

class StockScreenModel(
    private val assetRepository: UpdateInvestmentRepository,
    private val createInvestmentRepository: CreateInvestmentRepository,
) : ScreenModel {
    // 📦 ถังเก็บข้อมูล
    private val _state = MutableStateFlow(StockModel(
        stockName = "",
        quantity = FixedDecimal(0, 4),
        description = "",
        stockSymbol = "",
        brokerName = "",
        costPerPrice = Money(0),
        attachments = emptyList(),
        type = ""
    ))
    val state = _state.asStateFlow()
    private val udf = UiStateHolder(_state.value)
    val uiState: StateFlow<UiState<StockModel>> = udf.state
    val effects = udf.effects
    private var submitJob: Job? = null


    // ✍️ ฟังก์ชันอัปเดตข้อมูลจากหน้าฟอร์ม
    private val _addedAttachments = MutableStateFlow<List<Attachment>>(emptyList())
    private val _deleteAttachments = MutableStateFlow<List<Attachment>>(emptyList())

    fun onAction(action: FormAction<StockModel>) {
        when (action) {
            is FormAction.Changed -> updateForm(action.value)
            is FormAction.AttachmentsChanged -> updateAttachment(action.added, action.deleted)
            is FormAction.Submit -> submitAsset(action.id)
        }
    }


    fun updateForm(data: StockModel) {
        _state.update { it.copy(
            stockName = data.stockName,
            quantity = data.quantity,
            description = data.description,
            stockSymbol = data.stockSymbol,
            brokerName = data.brokerName,
            costPerPrice = data.costPerPrice,
            attachments = data.attachments,
            type = data.type
        ) }
        udf.set(_state.value, isLoading = false, error = null)
    }

    fun updateAttachment(addedList: List<Attachment>,deletedList: List<Attachment>) {
        _addedAttachments.update { addedList }
        _deleteAttachments.update { deletedList }
    }

    private fun asRequest(): InvestmentRequest {
        val current = _state.value



        // ✅ Map ข้อมูลให้มีทั้ง Byte, MimeType และ ชื่อไฟล์
        val allFiles = _addedAttachments.value.mapNotNull { attachment ->
            val bytes = attachment.platformData as? ByteArray ?: return@mapNotNull null

            // เช็กว่าเป็น PDF หรือ รูปภาพ
            val isPdf = attachment.name.endsWith(".pdf", ignoreCase = true) || attachment.type.toString().contains("PDF")
            val mimeType = if (isPdf) "application/pdf" else "image/jpeg"
            val extension = if (isPdf) "pdf" else "jpg"

            // ตั้งชื่อไฟล์ (เอา symbol มาต่อกับ index หรือเวลาเพื่อไม่ให้ซ้ำ)
            val fileName = "${current.stockName}.$extension"

            FileUploadData(bytes = bytes, mimeType = mimeType, fileName = fileName)
        }

        return InvestmentRequest(
            name = current.stockName,
            symbol = current.stockSymbol,
            type = current.type,
            quantity = current.quantity,
            costPerPrice = current.costPerPrice,
            brokerName = current.brokerName,
            description = current.description,
            files = allFiles,
            deleteListId = _deleteAttachments.value.map { it.id ?: "" }
        )
    }


    fun submitAsset(id:String,onSuccess: () -> Unit = {}) {
        if (submitJob?.isActive == true) return
        udf.loading()
        val job = screenModelScope.launch {
            try {

                val requestBody = asRequest()

                val assetResult = assetRepository.updateInvestment(id,requestBody)
                // ดึงข้อมูลออกมาจาก Result Wrapper
                val assetResponse = assetResult.getOrNull()

                if (assetResult.isSuccess && assetResponse != null) {
                    // ✅ ดึง ID ที่ได้จาก API ของการสร้าง Asset
                    // สมมติว่า field id อยู่ใน assetResponse.data.id หรือตาม Model ของคุณ
                    val createdItemId = assetResponse.id.toString()
                    udf.success()
                    udf.emit(FormEffect.Saved)
                    onSuccess()

                }
                else {
                    udf.failure(assetResult.exceptionOrNull()?.toAppError()
                        ?: IllegalStateException("Investment update failed").toAppError())
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

    /** Creates a new investment and returns its domain id to the share step. */
    fun submitCreate(onSuccess: (String) -> Unit = {}) {
        if (submitJob?.isActive == true) return
        udf.loading()
        val job = screenModelScope.launch {
            try {
                when (val result = createInvestmentRepository.create(asRequest().copy(deleteListId = emptyList()))) {
                    is com.wealthvault.core.architecture.AppResult.Success -> {
                        val id = result.value.id?.takeIf { it.isNotBlank() }
                            ?: error("Investment create response did not include an id")
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
