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
import kotlinx.coroutines.delay
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
            description = current?.description ?: "",
            locationAddress = current?.locationAddress ?: "",
            locationSubDistrict = current?.locationSubDistrict ?: "",
            locationDistrict = current?.locationDistrict ?: "",
            locationProvince = current?.locationProvince ?: "",
            locationPostalCode = current?.locationPostalCode ?: "",
            files = allFiles,
            referenceIds = allRefIds,
            deedNum = current?.deedNum ?: ""

        )
    }



    fun submitLand() {
        val shareToData = _state.value.shareTo ?: return
        screenModelScope.launch {
            try {
                isLoading = true
                errorMessage = null

                // --- ขั้นตอนที่ 1: สร้าง Land ก่อน ---

                val requestBody = asRequest()

                val landResult = landRepository.create(requestBody)

                // ดึงข้อมูลออกมาจาก Result Wrapper
                val landResponse = landResult.getOrNull()

                if (landResult.isSuccess && landResponse != null) {
                    // ✅ ดึง ID ที่ได้จาก API ของการสร้าง Land
                    // สมมติว่า field id อยู่ใน landResponse.data.id หรือตาม Model ของคุณ
                    val createdItemId = landResponse.id.toString()
                    println("✅ [ScreenModel] Land Created ID: $createdItemId")

                    delay(10000)
                    // --- ขั้นตอนที่ 2: เตรียมข้อมูลเพื่อ Share โดยใช้ ID ที่เพิ่งได้มา ---
                    val requestShareItem = ShareItemRequest(
                        itemIds = createdItemId, // 👈 ใส่ ID ที่ได้จากขั้นตอนที่ 1
                        itemTypes = "land",
                        emails = shareToData.email.map {
                            TargetItem(
                                id = it.name,
                                shareAt = shareToData.shareAt
                            )
                        },
                        friends = shareToData.friend.map {
                            TargetItem(
                                id = it.userId,
                                shareAt = shareToData.shareAt
                            )
                        },
                        groups = shareToData.group.map {
                            TargetItem(
                                id = it.userId,
                                shareAt = shareToData.shareAt
                            )
                        }
                    )

                    // --- ขั้นตอนที่ 3: ยิง API แชร์ทรัพย์สิน ---
                    val shareResult = shareItemRepository.shareItem(requestShareItem)
                    println(" [SummaryScreenModel] Share result: $shareResult")

                }
                else {
                    println("❌ [ScreenModel] Create Land Failed")
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
