package com.wealthvault.`financial-asset`.ui.cash.summary

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.core.architecture.FormEffect
import com.wealthvault.core.architecture.UiState
import com.wealthvault.core.architecture.UiStateHolder
import com.wealthvault.financialcommon.architecture.SummaryAction
import com.wealthvault.domain.portfolio.CashFileUploadData
import com.wealthvault.domain.portfolio.CashRequest
import com.wealthvault.domain.social.ShareItems
import com.wealthvault.domain.social.ShareTarget
import com.wealthvault.domain.portfolio.CreateCashRepository
import com.wealthvault.domain.social.ShareItemRepository
import com.wealthvault.domain.portfolio.CashModel
import com.wealthvault.domain.social.ShareTo
import com.wealthvault.core.model.Money
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


data class SummaryState(
    val cashRequest: CashModel? = null,
    val shareTo: ShareTo? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false
)
class CashSummaryScreenModel(
    private val cashRepository: CreateCashRepository,
    private val shareItemRepository: ShareItemRepository,
) : ScreenModel {

    private val _state = MutableStateFlow(SummaryState())
    val state = _state.asStateFlow()
    private val udf = UiStateHolder(_state.value)
    val uiState: StateFlow<UiState<SummaryState>> = udf.state
    val effects = udf.effects
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    private var submitJob: Job? = null
    fun initData(request: CashModel) {
        _state.update { it.copy(cashRequest = request) }
        syncUdf()

    }
    fun initShareInfo(request: ShareTo) {
        _state.update { it.copy(shareTo = request) }
        syncUdf()

    }
    fun onAction(action: SummaryAction<CashModel>, onSuccess: () -> Unit = {}) {
        when (action) {
            is SummaryAction.Changed -> initData(action.value)
            is SummaryAction.ShareChanged -> initShareInfo(action.value)
            SummaryAction.Submit -> submitCash(onSuccess)
        }
    }
    private fun syncUdf() {
        udf.set(_state.value)
    }
    private fun asRequest(): CashRequest {
        val current = _state.value.cashRequest

        // ✅ Map ข้อมูลให้มีทั้ง Byte, MimeType และ ชื่อไฟล์
        val allFiles = current?.attachments?.mapNotNull { attachment ->
            val bytes = attachment.platformData as? ByteArray ?: return@mapNotNull null

            // เช็กว่าเป็น PDF หรือ รูปภาพ
            val isPdf = attachment.name.endsWith(".pdf", ignoreCase = true) || attachment.type.toString().contains("PDF")
            val mimeType = if (isPdf) "application/pdf" else "image/jpeg"
            val extension = if (isPdf) "pdf" else "jpg"

            // ตั้งชื่อไฟล์ (เอา symbol มาต่อกับ index หรือเวลาเพื่อไม่ให้ซ้ำ)
            val fileName = "${current.cashName}.$extension"

            CashFileUploadData(bytes = bytes, mimeType = mimeType, fileName = fileName)
        } ?: emptyList()

        return CashRequest(
            name = current?.cashName ?: "",
            amount = current?.amount ?: Money(0),
            description = current?.description ?: "",
            files = allFiles
        )
    }



    fun submitCash(onSuccess: () -> Unit) {
        if (_state.value.isLoading || submitJob?.isActive == true) return
        val shareToData = _state.value.shareTo ?: return

        _state.update { it.copy(isLoading = true) }
        udf.loading()
        val job = screenModelScope.launch {
            try {
                errorMessage = null

                // --- ขั้นตอนที่ 1: สร้าง Cash ก่อน ---
                val requestBody = asRequest()
                val cashResult = cashRepository.create(requestBody)

                // ดึงข้อมูลออกมาจาก Result Wrapper
                val cashResponse = cashResult.getOrNull()

                if (cashResult.isSuccess && cashResponse != null) {
                    // ✅ ดึง ID ที่ได้จาก API ของการสร้าง Cash
                    val createdItemId = cashResponse.id.toString()

                    // 🚨 ลบ delay(10000) ทิ้งไปเรียบร้อยครับ!

                    // --- ขั้นตอนที่ 2: เตรียมข้อมูลเพื่อ Share โดยใช้ ID ที่เพิ่งได้มา ---
                    // 💡 เช็กก่อนว่ามีการเลือกคนแชร์หรือไม่ ถ้าไม่มีจะได้ข้าม API แชร์ไปเลย
                    val hasShareData = shareToData.email.isNotEmpty() ||
                            shareToData.friend.isNotEmpty() ||
                            shareToData.group.isNotEmpty()

                    if (hasShareData) {
                        val requestShareItem = ShareItems(
                            itemIds = createdItemId,
                            itemTypes = "cash",

                            // 🌟 1. แก้ email ให้ส่ง it.userId (ถ้า userId เก็บชื่ออีเมลไว้) และใช้วันที่ของแต่ละคน (it.apiDate)
                            emails = shareToData.email.map {
                                ShareTarget(id = it.userId, shareAt = it.apiDate)
                            },

                            // 🌟 2. ดึงวันที่ของเพื่อนแต่ละคน (it.apiDate) แบบเจาะจง
                            friends = shareToData.friend.map {
                                ShareTarget(id = it.userId, shareAt = it.apiDate)
                            },

                            // 🌟 3. ดึงวันที่ของกลุ่มแต่ละกลุ่ม (it.apiDate)
                            groups = shareToData.group.map {
                                ShareTarget(id = it.userId, shareAt = it.apiDate)
                            }
                        )

                        // --- ขั้นตอนที่ 3: ยิง API แชร์ทรัพย์สิน ---
                        shareItemRepository.shareItem(requestShareItem)
                    }

                    // 🌟 ส่งสัญญาณกลับไปหน้า UI ให้เด้งกลับหน้าแรก
                    udf.success(_state.value)
                    udf.emit(FormEffect.Saved)
                    onSuccess()
                } else {
                    val failure = cashResult.exceptionOrNull()
                        ?: IllegalStateException("Unable to create cash asset")
                    errorMessage = failure.message
                    udf.failure(com.wealthvault.core.architecture.AppError.Unknown(failure))
                }

            } catch (e: CancellationException) {
                throw e
            } catch (e: Throwable) {
                errorMessage = e.message ?: "เกิดข้อผิดพลาดในการเชื่อมต่อ"
                udf.failure(com.wealthvault.core.architecture.AppError.Unknown(e))
            } finally {
                // 🌟 2. อัปเดต StateFlow ปิดปุ่มโหลด
                _state.update { it.copy(isLoading = false) }
                syncUdf()
            }
        }
        submitJob = job
        job.invokeOnCompletion { if (submitJob === job) submitJob = null }
    }







}
