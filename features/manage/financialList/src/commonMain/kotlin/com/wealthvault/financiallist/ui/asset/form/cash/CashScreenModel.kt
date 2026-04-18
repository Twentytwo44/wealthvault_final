package com.wealthvault.financiallist.ui.asset.form.cash

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.cash_api.model.CashFileUploadData
import com.wealthvault.cash_api.model.CashRequest
import com.wealthvault.financiallist.data.cash.CashRepositoryImpl
import com.wealthvault_final.`financial-asset`.Imagepicker.Attachment
import com.wealthvault_final.`financial-asset`.model.CashModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CashScreenModel(
    private val cashRepository: CashRepositoryImpl
) : ScreenModel {
    // 📦 ถังเก็บข้อมูล

    private val _cashData = MutableStateFlow(CashModel(
        cashName = "",
        amount = 0.0,
        description = "",
        attachments = emptyList()
    ))
    private val _addedAttachments = MutableStateFlow<List<Attachment>>(emptyList())
    private val _deleteAttachments = MutableStateFlow<List<Attachment>>(emptyList())


    fun updateForm(data: CashModel) {
        println("data update succes " + data.cashName)
        _cashData.update { it.copy(cashName = data.cashName, amount = data.amount, description = data.description, attachments = data.attachments) }
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
            ammount = current.amount ,
            description = current.description,
            files = allFiles,
            deleteListId = _deleteAttachments.value.map { it.id ?: "" }
        )
    }



    fun submitCash(id:String) {
        screenModelScope.launch {
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
                    println("✅ [ScreenModel] Cash Created ID: $createdItemId")

                }
                else {
                    println("❌ [ScreenModel] Create Cash Failed")
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
