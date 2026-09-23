package com.wealthvault.`financial-obligations`.ui.expense.summary

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.core.architecture.FormEffect
import com.wealthvault.core.architecture.UiState
import com.wealthvault.core.architecture.UiStateHolder
import com.wealthvault.financialcommon.architecture.SummaryAction
import com.wealthvault.domain.portfolio.LiabilityRequest
import com.wealthvault.domain.portfolio.LiabilityUploadData
import com.wealthvault.domain.social.ShareItems
import com.wealthvault.domain.social.ShareTarget
import com.wealthvault.domain.social.ShareItemRepository
import com.wealthvault.domain.social.ShareTo
import com.wealthvault.core.model.Money
import com.wealthvault.core.model.FixedDecimal
import com.wealthvault.domain.portfolio.CreateLiabilityRepository
import com.wealthvault.domain.portfolio.ExpenseModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


data class ExpenseSummaryState(
    val expenseRequest: ExpenseModel? = null,
    val shareTo: ShareTo? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false
)
class ExpenseSummaryScreenModel(
    private val expenseRepository: CreateLiabilityRepository,
    private val shareItemRepository: ShareItemRepository,
) : ScreenModel {

    private val _state = MutableStateFlow(ExpenseSummaryState())
    val state = _state.asStateFlow()
    private val udf = UiStateHolder(_state.value)
    val uiState: StateFlow<UiState<ExpenseSummaryState>> = udf.state
    val effects = udf.effects
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    private var submitJob: Job? = null
    fun initData(request: ExpenseModel) {
        _state.update { it.copy(expenseRequest = request) }
        syncUdf()

    }
    fun initShareInfo(request: ShareTo) {
        _state.update { it.copy(shareTo = request) }
        syncUdf()

    }
    fun onAction(action: SummaryAction<ExpenseModel>, onSuccess: () -> Unit = {}) {
        when (action) {
            is SummaryAction.Changed -> initData(action.value)
            is SummaryAction.ShareChanged -> initShareInfo(action.value)
            SummaryAction.Submit -> submitExpense(onSuccess)
        }
    }
    private fun syncUdf() {
        udf.set(_state.value)
    }
    private fun asRequest(): LiabilityRequest {
        val current = _state.value.expenseRequest

        // ✅ Map ข้อมูลให้มีทั้ง Byte, MimeType และ ชื่อไฟล์
        val allFiles = current?.attachments?.mapNotNull { attachment ->
            val bytes = attachment.platformData as? ByteArray ?: return@mapNotNull null
            val isPdf = attachment.name.endsWith(".pdf", ignoreCase = true) || attachment.type.toString().contains("PDF")
            val mimeType = if (isPdf) "application/pdf" else "image/jpeg"
            val extension = if (isPdf) "pdf" else "jpg"
            val fileName = "${current.name}.$extension"

            LiabilityUploadData(bytes = bytes, mimeType = mimeType, fileName = fileName)
        } ?: emptyList()

        return LiabilityRequest(
            name = current?.name ?: "",
            type = "LIABILITY_TYPE_EXPENSE",

            // 🌟 กันเหนียวด้วย .takeIf เผื่อ Backend ไม่รับค่าว่าง String ("")
            description = current?.description?.takeIf { it.isNotBlank() },
            startedAt = current?.startedAt?.takeIf { it.isNotBlank() },
            endedAt = null,
            creditor = null,
            interestRate = current?.interestRate?.let { FixedDecimal.fromDecimal(it, scale = 4) },

            principal = current?.principal ?: Money(0),
            files = allFiles
        )
    }

    fun submitExpense(onSuccess: () -> Unit) {
        if (_state.value.isLoading || submitJob?.isActive == true) return
        val shareToData = _state.value.shareTo ?: return

        _state.update { it.copy(isLoading = true) }
        udf.loading()
        val job = screenModelScope.launch {
            try {
                errorMessage = null

                // --- ขั้นตอนที่ 1: สร้าง Expense ---
                val requestBody = asRequest()
                val expenseResult = expenseRepository.createLiability(requestBody)
                val expenseResponse = expenseResult.getOrNull()

                if (expenseResult.isSuccess && expenseResponse != null) {
                    val createdItemId = expenseResponse.id.toString()

                    // --- ขั้นตอนที่ 2: เตรียมข้อมูล Share ---
                    val hasShareData = shareToData.email.isNotEmpty() ||
                            shareToData.friend.isNotEmpty() ||
                            shareToData.group.isNotEmpty()

                    if (hasShareData) {
                        val requestShareItem = ShareItems(
                            itemIds = createdItemId,

                            // 🌟 แก้ตรงนี้เลยครับ! เปลี่ยนจาก "expense" เป็น "liability"
                            itemTypes = "liability",

                            emails = shareToData.email.map { ShareTarget(id = it.userId, shareAt = it.apiDate) },
                            friends = shareToData.friend.map { ShareTarget(id = it.userId, shareAt = it.apiDate) },
                            groups = shareToData.group.map { ShareTarget(id = it.userId, shareAt = it.apiDate) }
                        )

                        // --- ขั้นตอนที่ 3: ยิง API แชร์ทรัพย์สิน ---
                        shareItemRepository.shareItem(requestShareItem)

                        // 🌟 เช็กผลลัพธ์การแชร์ให้ชัดเจน
                    }

                    // ส่งสัญญาณกลับไปหน้า UI
                    udf.success(_state.value)
                    udf.emit(FormEffect.Saved)
                    onSuccess()
                } else {
                    val failure = expenseResult.exceptionOrNull()
                        ?: IllegalStateException("Unable to create expense")
                    errorMessage = failure.message
                    udf.failure(com.wealthvault.core.architecture.AppError.Unknown(failure))
                }

            } catch (e: CancellationException) {
                throw e
            } catch (e: Throwable) {
                errorMessage = e.message ?: "เกิดข้อผิดพลาดในการเชื่อมต่อ"
                udf.failure(com.wealthvault.core.architecture.AppError.Unknown(e))
            } finally {
                _state.update { it.copy(isLoading = false) }
                syncUdf()
            }
        }
        submitJob = job
        job.invokeOnCompletion { if (submitJob === job) submitJob = null }
    }







}
