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

        // ✅ Map ข้อมูลไฟล์
        val allFiles = current?.attachments?.mapNotNull { attachment ->
            val bytes = attachment.platformData as? ByteArray ?: return@mapNotNull null
            val isPdf = attachment.name.endsWith(".pdf", ignoreCase = true) || attachment.type.toString().contains("PDF")
            val mimeType = if (isPdf) "application/pdf" else "image/jpeg"
            val extension = if (isPdf) "pdf" else "jpg"
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

            // 🌟 ใช้ .takeIf { it.isNotBlank() } เพื่อบอกว่า "ถ้าว่าง = ไม่ต้องส่ง (null)"
            description = current?.description?.takeIf { it.isNotBlank() },
            locationAddress = current?.locationAddress?.takeIf { it.isNotBlank() },
            locationSubDistrict = current?.locationSubDistrict?.takeIf { it.isNotBlank() },
            locationDistrict = current?.locationDistrict?.takeIf { it.isNotBlank() },
            locationProvince = current?.locationProvince?.takeIf { it.isNotBlank() },
            locationPostalCode = current?.locationPostalCode?.takeIf { it.isNotBlank() },

            insIds = allInsRefIds,
            files = allFiles,
            referenceIds = allRefIds
        )
    }



    fun submitBuilding(onSuccess: () -> Unit) {
        val shareToData = _state.value.shareTo ?: return

        screenModelScope.launch {
            try {
                // 🌟 1. อัปเดต StateFlow เพื่อให้ UI แสดง Spinner โหลดที่ปุ่มได้ถูกต้อง
                _state.update { it.copy(isLoading = true) }
                errorMessage = null

                // --- ขั้นตอนที่ 1: สร้าง Building ก่อน ---
                val requestBody = asRequest()
                val buildingResult = buildingRepository.createBuilding(requestBody)

                // ดึงข้อมูลออกมาจาก Result Wrapper
                val buildingResponse = buildingResult.getOrNull()

                if (buildingResult.isSuccess && buildingResponse != null) {
                    // ✅ ดึง ID ที่ได้จาก API ของการสร้าง Building
                    val createdItemId = buildingResponse.id.toString()
                    println("✅ [ScreenModel] Building Created ID: $createdItemId")

                    // 🚨 ลบ delay(10000) ทิ้งเรียบร้อยครับ!

                    // --- ขั้นตอนที่ 2: เตรียมข้อมูลเพื่อ Share โดยใช้ ID ที่เพิ่งได้มา ---
                    // 💡 เช็กก่อนว่ามีการเลือกคนแชร์หรือไม่
                    val hasShareData = shareToData.email.isNotEmpty() ||
                            shareToData.friend.isNotEmpty() ||
                            shareToData.group.isNotEmpty()

                    if (hasShareData) {
                        val requestShareItem = ShareItemRequest(
                            itemIds = createdItemId,
                            itemTypes = "building",

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
                }
                else {
                    println("❌ [ScreenModel] Create Building Failed ${buildingResult}")
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
