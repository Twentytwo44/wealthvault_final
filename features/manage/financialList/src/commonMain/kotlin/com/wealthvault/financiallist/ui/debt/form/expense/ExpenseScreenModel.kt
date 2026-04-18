package com.wealthvault.financiallist.ui.debt.form.expense

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.financiallist.data.debt.LiabilityRepositoryImpl
import com.wealthvault.liability_api.model.LiabilityRequest
import com.wealthvault.liability_api.model.LiabilityUploadData
import com.wealthvault_final.`financial-asset`.Imagepicker.Attachment
import com.wealthvault_final.`financial-obligations`.model.ExpenseModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExpenseScreenModel(
    private val expenseRepository: LiabilityRepositoryImpl
) : ScreenModel {
    // 📦 ถังเก็บข้อมูล
    private val _state = MutableStateFlow(
        ExpenseModel(
            type = "",
            name = "",
            principal = 0.0,
            interestRate = "",
            description = "",
            startedAt = "",
            endedAt = "",
            creditor = "",
            attachments = emptyList()

        )
    )

    // ✍️ ฟังก์ชันอัปเดตข้อมูลจากหน้าฟอร์ม
    fun updateForm(data: ExpenseModel) {
        println("data update succes " + data.name)
        _state.update { it.copy(
            name = data.name,
            type = data.type,
            principal = data.principal,
            interestRate = "",
            description = data.description,
            startedAt = data.startedAt,
            endedAt = "",
            creditor = "",
            attachments = data.attachments

        ) }
    }

    private val _addedAttachments = MutableStateFlow<List<Attachment>>(emptyList())
    private val _deleteAttachments = MutableStateFlow<List<Attachment>>(emptyList())

    fun updateAttachment(addedList: List<Attachment>,deletedList: List<Attachment>) {
        _addedAttachments.update { addedList }
        _deleteAttachments.update { deletedList }
    }

    private fun asRequest(): LiabilityRequest {
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
            type = "LIABILITY_TYPE_EXPENSE",
            principal = current.principal,
            interestRate = current.interestRate,
            description = current.description,
            startedAt = current.startedAt,
            endedAt = current.endedAt,
            creditor = current.creditor,
            files = allFiles,
            deleteListId = _deleteAttachments.value.map { it.id ?: "" }
        )
    }



    fun submitLiability(id:String) {
        screenModelScope.launch {
            try {
//                isLoading = true
//                errorMessage = null

                // --- ขั้นตอนที่ 1: สร้าง Liability ก่อน ---

                val requestBody = asRequest()

                val liabilityResult = expenseRepository.updateLiability(id,requestBody)
                println("exp result: ${liabilityResult}")

                // ดึงข้อมูลออกมาจาก Result Wrapper
                val liabilityResponse = liabilityResult.getOrNull()

                if (liabilityResult.isSuccess && liabilityResponse != null) {
                    // ✅ ดึง ID ที่ได้จาก API ของการสร้าง Liability
                    // สมมติว่า field id อยู่ใน liabilityResponse.data.id หรือตาม Model ของคุณ
                    val createdItemId = liabilityResponse.id.toString()
                    println("✅ [ScreenModel] Liability Created ID: $createdItemId")

                }
                else {
                    println("❌ [ScreenModel] Create Liability Failed")
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
