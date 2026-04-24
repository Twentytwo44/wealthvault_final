package com.wealthvault_final.`financial-asset`.ui.bankaccount.summary

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.account_api.model.BankAccountFileUploadData
import com.wealthvault.account_api.model.BankAccountRequest
import com.wealthvault.share_api.model.ShareItemRequest
import com.wealthvault.share_api.model.TargetItem
import com.wealthvault_final.`financial-asset`.data.bankaccount.BankAccountRepositoryImpl
import com.wealthvault_final.`financial-asset`.data.share.ShareItemRepositoryImpl
import com.wealthvault_final.`financial-asset`.model.BankAccountModel
import com.wealthvault_final.`financial-asset`.model.ShareTo
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class BankAccountSummaryState(
    val bankAccountRequest: BankAccountModel? = null,
    val shareTo: ShareTo? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false
)
class BankAccountSummaryScreenModel(
    private val bankAccountRepository: BankAccountRepositoryImpl,
    private val shareItemRepository: ShareItemRepositoryImpl,
) : ScreenModel {

    private val _state = MutableStateFlow(BankAccountSummaryState())
    val state = _state.asStateFlow()
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    fun initData(request: BankAccountModel) {
        _state.update { it.copy(bankAccountRequest = request) }
        println("state"+ _state.value)

    }
    fun initShareInfo(request: ShareTo) {
        _state.update { it.copy(shareTo = request) }
        println("state"+ _state.value)

    }
    private fun asRequest(): BankAccountRequest {
        val current = _state.value.bankAccountRequest

        // ✅ Map ข้อมูลให้มีทั้ง Byte, MimeType และ ชื่อไฟล์
        val allFiles = current?.attachments?.mapNotNull { attachment ->
            val bytes = attachment.platformData as? ByteArray ?: return@mapNotNull null

            // เช็กว่าเป็น PDF หรือ รูปภาพ
            val isPdf = attachment.name.endsWith(".pdf", ignoreCase = true) || attachment.type.toString().contains("PDF")
            val mimeType = if (isPdf) "application/pdf" else "image/jpeg"
            val extension = if (isPdf) "pdf" else "jpg"

            // ตั้งชื่อไฟล์ (เอา symbol มาต่อกับ index หรือเวลาเพื่อไม่ให้ซ้ำ)
            val fileName = "${current.name}.$extension"

            BankAccountFileUploadData(bytes = bytes, mimeType = mimeType, fileName = fileName)
        } ?: emptyList()

        return BankAccountRequest(
            name = current?.name ?: "",
            bankName = current?.bankName ?: "",
            bankAccount = current?.bankId ?: "",
            type = current?.type ?: "",
            description = current?.description ?: "",
            amount = current?.amount ?: 0.0,
            files = allFiles
        )
    }



    // 🌟 1. เติม onSuccess: () -> Unit เข้าไปในวงเล็บ
    fun submitBankAccount(onSuccess: () -> Unit) {
        val shareToData = _state.value.shareTo ?: return
        screenModelScope.launch {
            try {
                isLoading = true
                errorMessage = null

                // --- ขั้นตอนที่ 1: สร้าง BankAccount ก่อน ---
                val requestBody = asRequest()
                val bankAccountResult = bankAccountRepository.createBankAccount(requestBody)
                val bankAccountResponse = bankAccountResult.getOrNull()

                if (bankAccountResult.isSuccess && bankAccountResponse != null) {
                    val createdItemId = bankAccountResponse.id.toString()
                    println("✅ [ScreenModel] BankAccount Created ID: $createdItemId")

                    // 💡 แอบกระซิบ: delay(10000) คือ 10 วินาทีเลยนะครับ!
                    // ถ้าแอปค้างหน้านี้นานๆ ตอนกดบันทึก เป็นเพราะบรรทัดนี้เลยครับ แนะนำให้เอาออกหรือลดเหลือ delay(500) พอครับ
                    delay(1000) // ผมขอลดเหลือ 1 วินาทีพอนะครับ จะได้ไม่รอนานเกินไป

                    // --- ขั้นตอนที่ 2: เตรียมข้อมูลเพื่อ Share โดยใช้ ID ที่เพิ่งได้มา ---
                    val requestShareItem = ShareItemRequest(
                        itemIds = createdItemId,
                        itemTypes = "bankAccount",
                        emails = shareToData.email.map { TargetItem(id = it.name, shareAt = shareToData.shareAt) },
                        friends = shareToData.friend.map { TargetItem(id = it.userId, shareAt = shareToData.shareAt) },
                        groups = shareToData.group.map { TargetItem(id = it.userId, shareAt = shareToData.shareAt) }
                    )

                    // --- ขั้นตอนที่ 3: ยิง API แชร์ทรัพย์สิน ---
                    val shareResult = shareItemRepository.shareItem(requestShareItem)
                    println(" [SummaryScreenModel] Share result: $shareResult")

                    // 🌟 2. เมื่อยิง API สร้างบัญชีและแชร์เสร็จหมดแล้ว ค่อยเรียก onSuccess() เพื่อเปลี่ยนหน้า!
                    onSuccess()

                } else {
                    println("❌ [ScreenModel] Create BankAccount Failed")
                }

            } catch (e: Exception) {
                println("❌ [ScreenModel] Exception: ${e.message}")
                errorMessage = e.message ?: "เกิดข้อผิดพลาดในการเชื่อมต่อ"
            } finally {
                isLoading = false
                println("🏁 [ScreenModel] Process Finished.")
            }
        }
    }







}
