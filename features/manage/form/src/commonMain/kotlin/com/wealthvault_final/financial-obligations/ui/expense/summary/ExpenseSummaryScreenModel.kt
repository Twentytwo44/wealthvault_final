package com.wealthvault_final.`financial-obligations`.ui.expense.summary

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.liability_api.model.LiabilityRequest
import com.wealthvault.liability_api.model.LiabilityUploadData
import com.wealthvault.share_api.model.ShareItemRequest
import com.wealthvault.share_api.model.TargetItem
import com.wealthvault_final.`financial-asset`.data.share.ShareItemRepositoryImpl
import com.wealthvault_final.`financial-asset`.model.ShareTo
import com.wealthvault_final.`financial-obligations`.data.liability.LiabilityRepositoryImpl
import com.wealthvault_final.`financial-obligations`.model.ExpenseModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class ExpenseSummaryState(
    val expenseRequest: ExpenseModel? = null,
    val shareTo: ShareTo? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false
)
class ExpenseSummaryScreenModel(
    private val expenseRepository: LiabilityRepositoryImpl,
    private val shareItemRepository: ShareItemRepositoryImpl,
) : ScreenModel {

    private val _state = MutableStateFlow(ExpenseSummaryState())
    val state = _state.asStateFlow()
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    fun initData(request: ExpenseModel) {
        _state.update { it.copy(expenseRequest = request) }
        println("state"+ _state.value)

    }
    fun initShareInfo(request: ShareTo) {
        _state.update { it.copy(shareTo = request) }
        println("state"+ _state.value)

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
            interestRate = null,

            principal = current?.principal ?: 0.0,
            files = allFiles
        )
    }

    fun submitExpense(onSuccess: () -> Unit) {
        val shareToData = _state.value.shareTo ?: return

        screenModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true) }
                errorMessage = null

                // --- ขั้นตอนที่ 1: สร้าง Expense ---
                val requestBody = asRequest()
                val expenseResult = expenseRepository.createLiability(requestBody)
                val expenseResponse = expenseResult.getOrNull()

                if (expenseResult.isSuccess && expenseResponse != null) {
                    val createdItemId = expenseResponse.id.toString()
                    println("✅ [ScreenModel] Expense Created ID: $createdItemId")

                    // --- ขั้นตอนที่ 2: เตรียมข้อมูล Share ---
                    val hasShareData = shareToData.email.isNotEmpty() ||
                            shareToData.friend.isNotEmpty() ||
                            shareToData.group.isNotEmpty()

                    if (hasShareData) {
                        val requestShareItem = ShareItemRequest(
                            itemIds = createdItemId,

                            // 🌟 แก้ตรงนี้เลยครับ! เปลี่ยนจาก "expense" เป็น "liability"
                            itemTypes = "liability",

                            emails = shareToData.email.map { TargetItem(id = it.userId, shareAt = it.apiDate) },
                            friends = shareToData.friend.map { TargetItem(id = it.userId, shareAt = it.apiDate) },
                            groups = shareToData.group.map { TargetItem(id = it.userId, shareAt = it.apiDate) }
                        )

                        // --- ขั้นตอนที่ 3: ยิง API แชร์ทรัพย์สิน ---
                        val shareResult = shareItemRepository.shareItem(requestShareItem)

                        // 🌟 เช็กผลลัพธ์การแชร์ให้ชัดเจน
                        if (shareResult.isSuccess) {
                            println("✅ [ScreenModel] Share Success!")
                        } else {
                            val shareErr = shareResult.exceptionOrNull()?.message ?: "Unknown Error"
                            println("❌ [ScreenModel] Share Failed!: $shareErr")
                        }
                    }

                    // ส่งสัญญาณกลับไปหน้า UI
                    onSuccess()
                } else {
                    val createErr = expenseResult.exceptionOrNull()?.message ?: "Unknown Error"
                    println("❌ [ScreenModel] Create Expense Failed: $createErr")
                }

            } catch (e: Exception) {
                println("❌ [ScreenModel] Exception: ${e.message}")
                errorMessage = e.message ?: "เกิดข้อผิดพลาดในการเชื่อมต่อ"
            } finally {
                _state.update { it.copy(isLoading = false) }
                println("🏁 [ScreenModel] Process Finished.")
            }
        }
    }







}
