package com.wealthvault_final.`financial-asset`.ui.realestate.building.summary

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.building_api.model.BuildingFileUploadData
import com.wealthvault.building_api.model.BuildingReferenceData
import com.wealthvault.building_api.model.BuildingRequest
import com.wealthvault.building_api.model.InsReferenceData
import com.wealthvault.share_api.model.ShareItemRequest
import com.wealthvault.share_api.model.TargetItem
import com.wealthvault_final.`financial-asset`.data.building.BuildingRepositoryImpl
import com.wealthvault_final.`financial-asset`.data.share.ShareItemRepositoryImpl
import com.wealthvault_final.`financial-asset`.model.BuildingModel
import com.wealthvault_final.`financial-asset`.model.ShareTo
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class BuildingSummaryState(
    val buildingRequest: BuildingModel? = null,
    val shareTo: ShareTo? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false
)
class BuildingSummaryScreenModel(
    private val buildingRepository: BuildingRepositoryImpl,
    private val shareItemRepository: ShareItemRepositoryImpl,
) : ScreenModel {

    private val _state = MutableStateFlow(BuildingSummaryState())
    val state = _state.asStateFlow()
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    fun initData(request: BuildingModel) {
        _state.update { it.copy(buildingRequest = request) }
        println("state"+ _state.value)

    }
    fun initShareInfo(request: ShareTo) {
        _state.update { it.copy(shareTo = request) }
        println("state"+ _state.value)

    }
    private fun asRequest(): BuildingRequest {
        val current = _state.value.buildingRequest

        // ✅ Map ข้อมูลให้มีทั้ง Byte, MimeType และ ชื่อไฟล์
        val allFiles = current?.attachments?.mapNotNull { attachment ->
            val bytes = attachment.platformData as? ByteArray ?: return@mapNotNull null

            // เช็กว่าเป็น PDF หรือ รูปภาพ
            val isPdf = attachment.name.endsWith(".pdf", ignoreCase = true) || attachment.type.toString().contains("PDF")
            val mimeType = if (isPdf) "application/pdf" else "image/jpeg"
            val extension = if (isPdf) "pdf" else "jpg"

            // ตั้งชื่อไฟล์ (เอา symbol มาต่อกับ index หรือเวลาเพื่อไม่ให้ซ้ำ)
            val fileName = "${current.buildingName}.$extension"

            BuildingFileUploadData(bytes = bytes, mimeType = mimeType, fileName = fileName)
        } ?: emptyList()

        val allRefIds = current?.referenceIds?.mapNotNull { ref ->
            BuildingReferenceData(areaName = ref.areaName, areaId = ref.areaId)
        } ?: emptyList()
        val allInsRefIds = current?.insIds?.mapNotNull { ref ->
            InsReferenceData(insName = ref.insName, insId = ref.insId)
        } ?: emptyList()

        return BuildingRequest(
            name = current?.buildingName ?: "",
            type = current?.type ?: "",
            area = current?.area ?: 0.0,
            amount = current?.amount ?: 0.0,
            description = current?.description ?: "",
            locationAddress = current?.locationAddress ?: "",
            locationSubDistrict = current?.locationSubDistrict ?: "",
            locationDistrict = current?.locationDistrict ?: "",
            locationProvince = current?.locationProvince ?: "",
            locationPostalCode = current?.locationPostalCode ?: "",
            insIds = allInsRefIds,
            files = allFiles,
            referenceIds = allRefIds
        )
    }



    fun submitBuilding(onSuccess: () -> Unit) {
        val shareToData = _state.value.shareTo ?: return
        screenModelScope.launch {
            try {
                isLoading = true
                errorMessage = null

                // --- ขั้นตอนที่ 1: สร้าง Building ก่อน ---

                val requestBody = asRequest()

                val buildingResult = buildingRepository.createBuilding(requestBody)

                // ดึงข้อมูลออกมาจาก Result Wrapper
                val buildingResponse = buildingResult.getOrNull()

                if (buildingResult.isSuccess && buildingResponse != null) {
                    // ✅ ดึง ID ที่ได้จาก API ของการสร้าง Building
                    // สมมติว่า field id อยู่ใน buildingResponse.data.id หรือตาม Model ของคุณ
                    val createdItemId = buildingResponse.id.toString()
                    println("✅ [ScreenModel] Building Created ID: $createdItemId")

                    delay(10000)
                    // --- ขั้นตอนที่ 2: เตรียมข้อมูลเพื่อ Share โดยใช้ ID ที่เพิ่งได้มา ---
                    val requestShareItem = ShareItemRequest(
                        itemIds = createdItemId, // 👈 ใส่ ID ที่ได้จากขั้นตอนที่ 1
                        itemTypes = "building",
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
                    onSuccess()
                }
                else {
                    println("❌ [ScreenModel] Create Building Failed ${buildingResult}")
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
