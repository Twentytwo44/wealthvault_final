package com.wealthvault.financiallist.ui.asset.form.account

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.account_api.model.BankAccountFileUploadData
import com.wealthvault.account_api.model.BankAccountRequest
import com.wealthvault.financiallist.data.account.BankAccountRepositoryImpl
import com.wealthvault_final.`financial-asset`.Imagepicker.Attachment
import com.wealthvault_final.`financial-asset`.model.BankAccountModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BankAccountScreenModel(
    private val bankAccountRepository: BankAccountRepositoryImpl
) : ScreenModel {
    // 📦 ถังเก็บข้อมูล
    private val _accountData = MutableStateFlow(
        BankAccountModel(
            type = "",
            bankName = "",
            bankId = "",
            name = "",
            amount = 0.0,
            description = "",
            attachments = emptyList()

        )
    )
    val state = _accountData.asStateFlow()

    // ✍️ ฟังก์ชันอัปเดตข้อมูลจากหน้าฟอร์ม
    private val _addedAttachments = MutableStateFlow<List<Attachment>>(emptyList())
    private val _deleteAttachments = MutableStateFlow<List<Attachment>>(emptyList())


    fun updateForm(data: BankAccountModel) {
        println("data update succes " + data.name)
        _accountData.update {
            it.copy(
               type = data.type,
                bankName = data.bankName,
                bankId = data.bankId,
                name = data.name,
                amount = data.amount,
                description = data.description,
                attachments = data.attachments,

            ) }
    }

    fun updateAttachment(addedList: List<Attachment>,deletedList: List<Attachment>) {
        _addedAttachments.update { addedList }
        _deleteAttachments.update { deletedList }
    }

    private fun asRequest(): BankAccountRequest {
        val current = _accountData.value



        // ✅ Map ข้อมูลให้มีทั้ง Byte, MimeType และ ชื่อไฟล์
        val allFiles = _addedAttachments.value.mapNotNull { attachment ->
            val bytes = attachment.platformData as? ByteArray ?: return@mapNotNull null

            // เช็กว่าเป็น PDF หรือ รูปภาพ
            val isPdf = attachment.name.endsWith(".pdf", ignoreCase = true) || attachment.type.toString().contains("PDF")
            val mimeType = if (isPdf) "application/pdf" else "image/jpeg"
            val extension = if (isPdf) "pdf" else "jpg"

            // ตั้งชื่อไฟล์ (เอา symbol มาต่อกับ index หรือเวลาเพื่อไม่ให้ซ้ำ)
            val fileName = "${current.name}.$extension"

            BankAccountFileUploadData(bytes = bytes, mimeType = mimeType, fileName = fileName)
        }

        return BankAccountRequest(
            name = current.name,
            type = current.type,
            bankName = current.bankName,
            bankAccount = current.bankId,
            amount = current.amount,
            description = current.description,
            files = allFiles,
            deleteListId = _deleteAttachments.value.map { it.id ?: "" }

        )
    }

    fun submitAccount(id:String) {
        screenModelScope.launch {
            try {
//                isLoading = true
//                errorMessage = null

                // --- ขั้นตอนที่ 1: สร้าง BankAccount ก่อน ---

                val requestBody = asRequest()

                val bankAccountResult = bankAccountRepository.updateAccount(id,requestBody)

                // ดึงข้อมูลออกมาจาก Result Wrapper
                val bankAccountResponse = bankAccountResult.getOrNull()

                if (bankAccountResult.isSuccess && bankAccountResponse != null) {
                    // ✅ ดึง ID ที่ได้จาก API ของการสร้าง BankAccount
                    // สมมติว่า field id อยู่ใน bankAccountResponse.data.id หรือตาม Model ของคุณ
                    val createdItemId = bankAccountResponse.id
                    println("✅ [ScreenModel] BankAccount Created ID: $createdItemId")

                }
                else {
                    println("❌ [ScreenModel] Create BankAccount Failed")
                }

            } catch (e: Exception) {
                println("❌ [ScreenModel] Exception: ${e.message}")
//                errorMessage = e.message ?: "เกิดข้อผิดพลาดในการเชื่อมต่อ"
            } finally {
//                isLoading = false
                println("🏁 [ScreenModel] Process Finished.")
            }
        }
    }

}
