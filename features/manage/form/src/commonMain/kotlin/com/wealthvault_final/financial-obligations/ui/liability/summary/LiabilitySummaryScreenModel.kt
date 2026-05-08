package com.wealthvault_final.`financial-obligations`.ui.liability.summary

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
import com.wealthvault_final.`financial-obligations`.model.LiabilityModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class LiabilitySummaryState(
    val liabilityRequest: LiabilityModel? = null,
    val shareTo: ShareTo? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false
)
class LiabilitySummaryScreenModel(
    private val liabilityRepository: LiabilityRepositoryImpl,
    private val shareItemRepository: ShareItemRepositoryImpl,
) : ScreenModel {

    private val _state = MutableStateFlow(LiabilitySummaryState())
    val state = _state.asStateFlow()
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    fun initData(request: LiabilityModel) {
        _state.update { it.copy(liabilityRequest = request) }
        println("state"+ _state.value)

    }
    fun initShareInfo(request: ShareTo) {
        _state.update { it.copy(shareTo = request) }
        println("state"+ _state.value)

    }
    private fun asRequest(): LiabilityRequest {
        val current = _state.value.liabilityRequest

        // ✅ Map ข้อมูลให้มีทั้ง Byte, MimeType และ ชื่อไฟล์
        val allFiles = current?.attachments?.mapNotNull { attachment ->
            val bytes = attachment.platformData as? ByteArray ?: return@mapNotNull null

            // เช็กว่าเป็น PDF หรือ รูปภาพ
            val isPdf = attachment.name.endsWith(".pdf", ignoreCase = true) || attachment.type.toString().contains("PDF")
            val mimeType = if (isPdf) "application/pdf" else "image/jpeg"
            val extension = if (isPdf) "pdf" else "jpg"

            // ตั้งชื่อไฟล์ (เอา symbol มาต่อกับ index หรือเวลาเพื่อไม่ให้ซ้ำ)
            val fileName = "${current.name}.$extension"

            LiabilityUploadData(bytes = bytes, mimeType = mimeType, fileName = fileName)
        } ?: emptyList()

        return LiabilityRequest(
            name = current?.name ?: "",
            type = "LIABILITY_TYPE_DEBT",
            description = current?.description ?: "",
            startedAt = current?.startedAt ?: "",
            endedAt = current?.endedAt ?: "",
            creditor = current?.creditor ?: "",
            interestRate = current?.interestRate ?: "",
            principal = current?.principal ?: 0.0,
            files = allFiles
        )
    }



    fun submitLiability(onSuccess: () -> Unit) {
        val shareToData = _state.value.shareTo ?: return

        screenModelScope.launch {
            try {
                // 🌟 1. แก้ไขให้ StateFlow อัปเดต UI จะได้โชว์ปุ่มกำลังโหลด (Spinner) ถูกต้อง
                _state.update { it.copy(isLoading = true) }
                errorMessage = null

                // --- ขั้นตอนที่ 1: สร้าง Liability ก่อน ---
                val requestBody = asRequest()
                val liabilityResult = liabilityRepository.createLiability(requestBody)
                val liabilityResponse = liabilityResult.getOrNull()

                if (liabilityResult.isSuccess && liabilityResponse != null) {
                    val createdItemId = liabilityResponse.id.toString()
                    println("✅ [ScreenModel] Liability Created ID: $createdItemId")

                    // 🚨 ลบ delay(10000) ทิ้งไปแล้ว! แอปจะไม่ค้างแล้วครับ

                    // --- ขั้นตอนที่ 2: เตรียมข้อมูลเพื่อ Share โดยใช้ ID ที่เพิ่งได้มา ---
                    // 💡 เช็กก่อนว่ามีการแชร์ไหม ถ้าไม่มีจะได้ไม่ต้องยิง API แชร์ให้เสียเวลา
                    val hasShareData = shareToData.email.isNotEmpty() ||
                            shareToData.friend.isNotEmpty() ||
                            shareToData.group.isNotEmpty()

                    if (hasShareData) {
                        val requestShareItem = ShareItemRequest(
                            itemIds = createdItemId,
                            itemTypes = "liability",

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
                    println("❌ [ScreenModel] Create Liability Failed")
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
