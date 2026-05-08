package com.wealthvault_final.`financial-asset`.ui.realestate.land.summary

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.land_api.model.LandFileUploadData
import com.wealthvault.land_api.model.LandReferenceData
import com.wealthvault.land_api.model.LandRequest
import com.wealthvault.share_api.model.ShareItemRequest
import com.wealthvault.share_api.model.TargetItem
import com.wealthvault_final.`financial-asset`.data.land.LandRepositoryImpl
import com.wealthvault_final.`financial-asset`.data.share.ShareItemRepositoryImpl
import com.wealthvault_final.`financial-asset`.model.LandModel
import com.wealthvault_final.`financial-asset`.model.ShareTo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class LandSummaryState(
    val landRequest: LandModel? = null,
    val shareTo: ShareTo? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false
)
class LandSummaryScreenModel(
    private val landRepository: LandRepositoryImpl,
    private val shareItemRepository: ShareItemRepositoryImpl,
) : ScreenModel {

    private val _state = MutableStateFlow(LandSummaryState())
    val state = _state.asStateFlow()
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    fun initData(request: LandModel) {
        _state.update { it.copy(landRequest = request) }
        println("state"+ _state.value)

    }
    fun initShareInfo(request: ShareTo) {
        _state.update { it.copy(shareTo = request) }
        println("state"+ _state.value)

    }
    private fun asRequest(): LandRequest {
        val current = _state.value.landRequest

        // ✅ Map ข้อมูลให้มีทั้ง Byte, MimeType และ ชื่อไฟล์
        val allFiles = current?.attachments?.mapNotNull { attachment ->
            val bytes = attachment.platformData as? ByteArray ?: return@mapNotNull null

            // เช็กว่าเป็น PDF หรือ รูปภาพ
            val isPdf = attachment.name.endsWith(".pdf", ignoreCase = true) || attachment.type.toString().contains("PDF")
            val mimeType = if (isPdf) "application/pdf" else "image/jpeg"
            val extension = if (isPdf) "pdf" else "jpg"

            // ตั้งชื่อไฟล์ (เอา symbol มาต่อกับ index หรือเวลาเพื่อไม่ให้ซ้ำ)
            val fileName = "${current.landName}.$extension"

            LandFileUploadData(bytes = bytes, mimeType = mimeType, fileName = fileName)
        } ?: emptyList()

        val allRefIds = current?.referenceIds?.mapNotNull { ref ->
            LandReferenceData(areaName = ref.areaName, areaId = ref.areaId)
        } ?: emptyList()

        return LandRequest(
            name = current?.landName ?: "",
            area = current?.area ?: 0.0,
            amount = current?.amount ?: 0.0,

            // 🌟 ใช้ .takeIf { it.isNotBlank() } เพื่อถ้าเป็นค่าว่างให้เปลี่ยนเป็น null
            description = current?.description?.takeIf { it.isNotBlank() },
            locationAddress = current?.locationAddress?.takeIf { it.isNotBlank() },
            locationSubDistrict = current?.locationSubDistrict?.takeIf { it.isNotBlank() },
            locationDistrict = current?.locationDistrict?.takeIf { it.isNotBlank() },
            locationProvince = current?.locationProvince?.takeIf { it.isNotBlank() },
            locationPostalCode = current?.locationPostalCode?.takeIf { it.isNotBlank() },

            // เลขโฉนดก็อาจจะว่างได้ ถ้าไม่ได้บังคับให้กรอกก็ใส่ไว้ด้วยครับ
            deedNum = current?.deedNum?.takeIf { it.isNotBlank() },

            files = allFiles,
            referenceIds = allRefIds
        )
    }

    fun submitLand(onSuccess: () -> Unit) {
        val shareToData = _state.value.shareTo ?: return

        screenModelScope.launch {
            try {
                // 🌟 1. อัปเดต StateFlow เพื่อให้ UI แสดง Spinner โหลดที่ปุ่มได้ถูกต้อง
                _state.update { it.copy(isLoading = true) }
                errorMessage = null

                // --- ขั้นตอนที่ 1: สร้าง Land ก่อน ---
                val requestBody = asRequest()
                val landResult = landRepository.create(requestBody)

                // ดึงข้อมูลออกมาจาก Result Wrapper
                val landResponse = landResult.getOrNull()

                if (landResult.isSuccess && landResponse != null) {
                    // ✅ ดึง ID ที่ได้จาก API ของการสร้าง Land
                    val createdItemId = landResponse.id.toString()
                    println("✅ [ScreenModel] Land Created ID: $createdItemId")

                    // --- ขั้นตอนที่ 2: เตรียมข้อมูลเพื่อ Share โดยใช้ ID ที่เพิ่งได้มา ---
                    val hasShareData = shareToData.email.isNotEmpty() ||
                            shareToData.friend.isNotEmpty() ||
                            shareToData.group.isNotEmpty()

                    if (hasShareData) {
                        val requestShareItem = ShareItemRequest(
                            itemIds = createdItemId,
                            itemTypes = "land",

                            emails = shareToData.email.map { TargetItem(id = it.userId, shareAt = it.apiDate) },
                            friends = shareToData.friend.map { TargetItem(id = it.userId, shareAt = it.apiDate) },
                            groups = shareToData.group.map { TargetItem(id = it.userId, shareAt = it.apiDate) }
                        )

                        // --- ขั้นตอนที่ 3: ยิง API แชร์ทรัพย์สิน ---
                        val shareResult = shareItemRepository.shareItem(requestShareItem)
                        println(" [SummaryScreenModel] Share result: $shareResult")
                    }

                    // 🌟 ส่งสัญญาณกลับไปหน้า UI ให้เด้งกลับหน้าแรก
                    onSuccess()
                } else {
                    // 🌟 ดึงข้อความ Error จริงๆ จากระบบออกมาดูเผื่อมีปัญหาอื่นอีก
                    val errorDetail = landResult.exceptionOrNull()?.message ?: "ไม่ทราบสาเหตุ"
                    println("❌ [ScreenModel] Create Land Failed!")
                    println("🚨 รายละเอียด Error: $errorDetail")
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
