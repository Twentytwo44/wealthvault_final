package com.wealthvault.financiallist.ui.asset.form.investment

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.financiallist.data.investment.AssetRepositoryImpl
import com.wealthvault.investment_api.model.FileUploadData
import com.wealthvault.investment_api.model.InvestmentRequest
import com.wealthvault_final.`financial-asset`.Imagepicker.Attachment
import com.wealthvault_final.`financial-asset`.model.StockModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class StockScreenModel(
    private val assetRepository: AssetRepositoryImpl
) : ScreenModel {
    // 📦 ถังเก็บข้อมูล
    private val _state = MutableStateFlow(StockModel(
        stockName = "",
        quantity = 0.00,
        description = "",
        stockSymbol = "",
        brokerName = "",
        costPerPrice = 0.00,
        attachments = emptyList(),
        type = ""
    ))


    // ✍️ ฟังก์ชันอัปเดตข้อมูลจากหน้าฟอร์ม
    private val _addedAttachments = MutableStateFlow<List<Attachment>>(emptyList())
    private val _deleteAttachments = MutableStateFlow<List<Attachment>>(emptyList())


    fun updateForm(data: StockModel) {
        println("data update succes " + data.stockName)
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
            quantity = current.quantity.toString(),
            costPerPrice = current.costPerPrice.toString(),
            brokerName = current.brokerName,
            description = current.description,
            files = allFiles,
            deleteListId = _deleteAttachments.value.map { it.id ?: "" }
        )
    }


    fun submitAsset(id:String) {
        screenModelScope.launch {
            try {

                val requestBody = asRequest()

                val assetResult = assetRepository.updateInvestment(id,requestBody)
                println("response asset: ${assetResult}")
                // ดึงข้อมูลออกมาจาก Result Wrapper
                val assetResponse = assetResult.getOrNull()

                if (assetResult.isSuccess && assetResponse != null) {
                    // ✅ ดึง ID ที่ได้จาก API ของการสร้าง Asset
                    // สมมติว่า field id อยู่ใน assetResponse.data.id หรือตาม Model ของคุณ
                    val createdItemId = assetResponse.id.toString()
                    println("✅ [ScreenModel] Asset Created ID: $createdItemId")

                }
                else {
                    println("❌ [ScreenModel] Create Asset Failed")
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
