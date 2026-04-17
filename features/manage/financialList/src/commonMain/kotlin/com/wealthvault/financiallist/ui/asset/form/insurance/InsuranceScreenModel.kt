package com.wealthvault.financiallist.ui.asset.form.insurance

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.financiallist.data.insurance.InsuranceRepositoryImpl
import com.wealthvault.insurance_api.model.InsuranceFileUploadData
import com.wealthvault.insurance_api.model.InsuranceRequest
import com.wealthvault_final.`financial-asset`.Imagepicker.Attachment
import com.wealthvault_final.`financial-asset`.model.InsuranceModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class InsuranceScreenModel(
    private val insuranceRepository: InsuranceRepositoryImpl
) : ScreenModel {
    // 📦 ถังเก็บข้อมูล
    private val _state = MutableStateFlow(
        InsuranceModel(
            policyNumber = "",
            type = "",
            companyName = "",
            coverageAmount = 0.0,
            coveragePeriod = "",
            expDate = "",
            description = "",
            name = "",
            attachments = emptyList(),
            conDate = ""
        )
    )
    private val _addedAttachments = MutableStateFlow<List<Attachment>>(emptyList())
    private val _deleteAttachments = MutableStateFlow<List<Attachment>>(emptyList())


    fun updateForm(data: InsuranceModel) {
        println("data update succes " + data.name)
        _state.update { it.copy(
            policyNumber = data.policyNumber,
            type = data.type,
            companyName = data.companyName,
            coverageAmount = data.coverageAmount,
            coveragePeriod = data.coveragePeriod,
            expDate = data.expDate,
            description = data.description,
            name = data.name,
            attachments = data.attachments,
            conDate = data.conDate
        ) }
    }

    fun updateAttachment(addedList: List<Attachment>,deletedList: List<Attachment>) {
        _addedAttachments.update { addedList }
        _deleteAttachments.update { deletedList }
    }

    private fun asRequest(): InsuranceRequest {
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

            InsuranceFileUploadData(bytes = bytes, mimeType = mimeType, fileName = fileName)
        }

        return InsuranceRequest(
            name = current.name,
            policyNumber = current.policyNumber,
            type = current.type,
            companyName = current.companyName,
            coverageAmount = current.coverageAmount,
            coveragePeriod = current.coveragePeriod,
            conDate = current.conDate,
            expDate = current.expDate,
            description = current.description,
            files = allFiles,
            deleteListId = _deleteAttachments.value.map { it.id ?: "" }
        )
    }



    fun submitInsurance(id:String) {
        screenModelScope.launch {
            try {
//                isLoading = true
//                errorMessage = null

                // --- ขั้นตอนที่ 1: สร้าง insurance ก่อน ---

                val requestBody = asRequest()

                val insuranceResult = insuranceRepository.updateInsurance(id,requestBody)

                // ดึงข้อมูลออกมาจาก Result Wrapper
                val insuranceResponse = insuranceResult.getOrNull()

                if (insuranceResult.isSuccess && insuranceResponse != null) {
                    // ✅ ดึง ID ที่ได้จาก API ของการสร้าง insurance
                    // สมมติว่า field id อยู่ใน insuranceResponse.data.id หรือตาม Model ของคุณ
                    val createdItemId = insuranceResponse.id.toString()
                    println("✅ [ScreenModel] insurance Created ID: $createdItemId")

                }
                else {
                    println("❌ [ScreenModel] Create insurance Failed")
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
