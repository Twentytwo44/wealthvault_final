package com.wealthvault_final.`financial-asset`.ui.cash.summary

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.cash_api.model.CashFileUploadData
import com.wealthvault.cash_api.model.CashRequest
import com.wealthvault.share_api.model.ShareItemRequest
import com.wealthvault.share_api.model.TargetItem
import com.wealthvault_final.`financial-asset`.data.cash.CashRepositoryImpl
import com.wealthvault_final.`financial-asset`.data.share.ShareItemRepositoryImpl
import com.wealthvault_final.`financial-asset`.model.CashModel
import com.wealthvault_final.`financial-asset`.model.ShareTo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class SummaryState(
    val cashRequest: CashModel? = null,
    val shareTo: ShareTo? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false
)
class CashSummaryScreenModel(
    private val cashRepository: CashRepositoryImpl,
    private val shareItemRepository: ShareItemRepositoryImpl,
) : ScreenModel {

    private val _state = MutableStateFlow(SummaryState())
    val state = _state.asStateFlow()
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    fun initData(request: CashModel) {
        _state.update { it.copy(cashRequest = request) }
        println("state"+ _state.value)

    }
    fun initShareInfo(request: ShareTo) {
        _state.update { it.copy(shareTo = request) }
        println("state"+ _state.value)

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
            amount = current?.amount ?: 0.0,
            description = current?.description ?: "",
            files = allFiles
        )
    }



    fun submitCash(onSuccess: () -> Unit) {
        val shareToData = _state.value.shareTo ?: return

        screenModelScope.launch {
            try {
                // 🌟 1. อัปเดต StateFlow เพื่อให้ UI แสดง Spinner โหลดที่ปุ่มได้ถูกต้อง
                _state.update { it.copy(isLoading = true) }
                errorMessage = null

                // --- ขั้นตอนที่ 1: สร้าง Cash ก่อน ---
                val requestBody = asRequest()
                val cashResult = cashRepository.create(requestBody)

                // ดึงข้อมูลออกมาจาก Result Wrapper
                val cashResponse = cashResult.getOrNull()

                if (cashResult.isSuccess && cashResponse != null) {
                    // ✅ ดึง ID ที่ได้จาก API ของการสร้าง Cash
                    val createdItemId = cashResponse.id.toString()
                    println("✅ [ScreenModel] Cash Created ID: $createdItemId")

                    // 🚨 ลบ delay(10000) ทิ้งไปเรียบร้อยครับ!

                    // --- ขั้นตอนที่ 2: เตรียมข้อมูลเพื่อ Share โดยใช้ ID ที่เพิ่งได้มา ---
                    // 💡 เช็กก่อนว่ามีการเลือกคนแชร์หรือไม่ ถ้าไม่มีจะได้ข้าม API แชร์ไปเลย
                    val hasShareData = shareToData.email.isNotEmpty() ||
                            shareToData.friend.isNotEmpty() ||
                            shareToData.group.isNotEmpty()

                    if (hasShareData) {
                        val requestShareItem = ShareItemRequest(
                            itemIds = createdItemId,
                            itemTypes = "cash",

                            // 🌟 1. แก้ email ให้ส่ง it.userId (ถ้า userId เก็บชื่ออีเมลไว้) และใช้วันที่ของแต่ละคน (it.apiDate)
                            emails = shareToData.email.map {
                                TargetItem(id = it.userId, shareAt = it.apiDate)
                            },

                            // 🌟 2. ดึงวันที่ของเพื่อนแต่ละคน (it.apiDate) แบบเจาะจง
                            friends = shareToData.friend.map {
                                TargetItem(id = it.userId, shareAt = it.apiDate)
                            },

                            // 🌟 3. ดึงวันที่ของกลุ่มแต่ละกลุ่ม (it.apiDate)
                            groups = shareToData.group.map {
                                TargetItem(id = it.userId, shareAt = it.apiDate)
                            }
                        )

                        // --- ขั้นตอนที่ 3: ยิง API แชร์ทรัพย์สิน ---
                        val shareResult = shareItemRepository.shareItem(requestShareItem)
                        println(" [SummaryScreenModel] Share result: $shareResult")
                    }

                    // 🌟 ส่งสัญญาณกลับไปหน้า UI ให้เด้งกลับหน้าแรก
                    onSuccess()
                } else {
                    val errorDetail = cashResult.exceptionOrNull()?.message ?: "ไม่ทราบสาเหตุ"
                    val errorCause = cashResult.exceptionOrNull()?.cause?.message ?: ""
                    println("❌ [ScreenModel] Create Cash Failed!")
                    println("🚨 รายละเอียด Error: $errorDetail")
                    println("🚨 สาเหตุเชิงลึก: $errorCause")
                }

            } catch (e: Exception) {
                println("❌ [ScreenModel] Exception: ${e.message}")
                errorMessage = e.message ?: "เกิดข้อผิดพลาดในการเชื่อมต่อ"
            } finally {
                // 🌟 2. อัปเดต StateFlow ปิดปุ่มโหลด
                _state.update { it.copy(isLoading = false) }
                println("🏁 [ScreenModel] Process Finished.")
            }
        }
    }







}
