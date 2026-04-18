package com.wealthvault.financiallist.ui.asset.form.land

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.building_api.model.GetBuildingData
import com.wealthvault.financiallist.data.land.LandRepositoryImpl
import com.wealthvault.land_api.model.LandFileUploadData
import com.wealthvault.land_api.model.LandReferenceData
import com.wealthvault.land_api.model.LandRequest
import com.wealthvault_final.`financial-asset`.Imagepicker.Attachment
import com.wealthvault_final.`financial-asset`.data.building.GetBuildingRepositoryImpl
import com.wealthvault_final.`financial-asset`.model.LandModel
import com.wealthvault_final.`financial-asset`.model.RefModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LandScreenModel(
    private val buildingRepository: GetBuildingRepositoryImpl,
    private val landRepository: LandRepositoryImpl,
) : ScreenModel {
    // 📦 ถังเก็บข้อมูล
    private val _state = MutableStateFlow(

        LandModel(
            deedNum = "",
            landName = "",
            area = 0.0,
            amount = 0.0,
            description = "",
            attachments = emptyList(),
            referenceIds = emptyList(),
            locationAddress = "",
            locationSubDistrict = "",
            locationDistrict = "",
            locationProvince = "",
            locationPostalCode = "",

        )
    )
    val state = _state.asStateFlow()

    private val _BuildingState = MutableStateFlow<List<GetBuildingData>>(emptyList())
    val BuildingState = _BuildingState.asStateFlow()




    // ✍️ ฟังก์ชันอัปเดตข้อมูลจากหน้าฟอร์ม
    fun updateForm(data: LandModel) {
        println("data update succes " + data.landName)
        _state.update { it.copy(
            deedNum = data.description,
            landName = data.landName,
            area = data.area,
            amount = data.amount,
            description = data.description,
            attachments = data.attachments,
            referenceIds = data.referenceIds,
            locationAddress = data.locationAddress,
            locationSubDistrict = data.locationSubDistrict,
            locationDistrict = data.locationDistrict,
            locationProvince = data.locationProvince,
            locationPostalCode = data.locationPostalCode,
        ) }
    }

    fun fetchData() {
        screenModelScope.launch {

            val buildingDeferred = async { buildingRepository.getBuilding() }

            // รอรับผลลัพธ์จากทั้ง 2 API
            val buildingResult = buildingDeferred.await()


            buildingResult.onSuccess { buildingData ->
                _BuildingState.value = buildingData
                println("✅ Screen Model Land Data: ${buildingData}")
            }.onFailure { error ->
                println("❌ Failed to get lands: ${error.message}")
            }

        }
    }

    private val _addedAttachments = MutableStateFlow<List<Attachment>>(emptyList())
    private val _deleteAttachments = MutableStateFlow<List<Attachment>>(emptyList())

    private val _addedRef = MutableStateFlow<List<RefModel>>(emptyList())
    private val _deleteRef = MutableStateFlow<List<RefModel>>(emptyList())





    fun updateAttachment(addedList: List<Attachment>,deletedList: List<Attachment>,addedRef:List<RefModel>,deletedRef:List<RefModel>) {
        _addedAttachments.update { addedList }
        _deleteAttachments.update { deletedList }
        _addedRef.update { addedRef }
        _deleteRef.update { deletedRef }
    }

    private fun asRequest(): LandRequest {
        val current = _state.value



        // ✅ Map ข้อมูลให้มีทั้ง Byte, MimeType และ ชื่อไฟล์
        val allFiles = _addedAttachments.value.mapNotNull { attachment ->
            val bytes = attachment.platformData as? ByteArray ?: return@mapNotNull null

            // เช็กว่าเป็น PDF หรือ รูปภาพ
            val isPdf = attachment.name.endsWith(".pdf", ignoreCase = true) || attachment.type.toString().contains("PDF")
            val mimeType = if (isPdf) "application/pdf" else "image/jpeg"
            val extension = if (isPdf) "pdf" else "jpg"

            // ตั้งชื่อไฟล์ (เอา symbol มาต่อกับ index หรือเวลาเพื่อไม่ให้ซ้ำ)
            val fileName = "${current.landName}.$extension"

            LandFileUploadData(bytes = bytes, mimeType = mimeType, fileName = fileName)
        }

        return LandRequest(
            name = current.landName,
            deedNum = current.deedNum,
            area = current.area,
            amount = current.amount,
            description = current.description,
            locationAddress = current.locationAddress,
            locationSubDistrict = current.locationSubDistrict,
            locationDistrict = current.locationDistrict,
            locationProvince = current.locationProvince,
            locationPostalCode = current.locationPostalCode,
            files = allFiles,
            referenceIds = _addedRef.value.map { data ->
                LandReferenceData(
                    areaName = data.areaName,
                    areaId = data.areaId
                ) },
            deleteRefListId = _deleteRef.value.map {data ->
                LandReferenceData(
                    areaName = data.areaName,
                    areaId = data.areaId
                )
            },
            deleteListId = _deleteAttachments.value.map { it.id ?: "" }
        )
    }



    fun submitLand(id:String) {
        screenModelScope.launch {
            try {
//                isLoading = true
//                errorMessage = null

                // --- ขั้นตอนที่ 1: สร้าง Land ก่อน ---

                val requestBody = asRequest()

                val landResult = landRepository.updateLand(id,requestBody)
                println("land result: ${landResult}")
                // ดึงข้อมูลออกมาจาก Result Wrapper
                val landResponse = landResult.getOrNull()

                if (landResult.isSuccess && landResponse != null) {
                    // ✅ ดึง ID ที่ได้จาก API ของการสร้าง Land
                    // สมมติว่า field id อยู่ใน landResponse.data.id หรือตาม Model ของคุณ
                    val createdItemId = landResponse.id.toString()
                    println("✅ [ScreenModel] Land Created ID: $createdItemId")

                }
                else {
                    println("❌ [ScreenModel] Create Land Failed")
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
