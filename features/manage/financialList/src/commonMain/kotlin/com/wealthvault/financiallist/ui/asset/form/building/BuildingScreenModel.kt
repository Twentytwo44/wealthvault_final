package com.wealthvault.financiallist.ui.asset.form.building

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.building_api.model.BuildingFileUploadData
import com.wealthvault.building_api.model.BuildingReferenceData
import com.wealthvault.building_api.model.BuildingRequest
import com.wealthvault.building_api.model.InsReferenceData
import com.wealthvault.financiallist.data.building.BuildingRepositoryImpl
import com.wealthvault.insurance_api.model.GetInsuranceData
import com.wealthvault.land_api.model.GetLandData
import com.wealthvault_final.`financial-asset`.Imagepicker.Attachment
import com.wealthvault_final.`financial-asset`.data.insurance.GetInsuranceRepositoryImpl
import com.wealthvault_final.`financial-asset`.data.land.GetLandRepositoryImpl
import com.wealthvault_final.`financial-asset`.model.BuildingModel
import com.wealthvault_final.`financial-asset`.model.InsRefModel
import com.wealthvault_final.`financial-asset`.model.RefModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BuildingScreenModel(
    private val landRepository: GetLandRepositoryImpl,
    private val insuranceRepository: GetInsuranceRepositoryImpl,
    private val buildingRepository: BuildingRepositoryImpl
) : ScreenModel {

    private val _LandState = MutableStateFlow<List<GetLandData>>(emptyList())
    val LandState = _LandState.asStateFlow()

    private val _InsState = MutableStateFlow<List<GetInsuranceData>>(emptyList())
    val InsState = _InsState.asStateFlow()


    init {
        fetchData()
    }


    private val _state = MutableStateFlow(

        BuildingModel(
            type = "",
            buildingName = "",
            area = 0.0,
            amount = 0.0,
            description = "",
            attachments = emptyList(),
            referenceIds =  emptyList(),
            locationAddress = "",
            locationSubDistrict = "",
            locationDistrict = "",
            locationProvince = "",
            locationPostalCode = "",
            insIds = emptyList(),

        )
    )
    val state = _state.asStateFlow()

    // ✍️ ฟังก์ชันอัปเดตข้อมูลจากหน้าฟอร์ม
    fun updateForm(data: BuildingModel) {
        println("data update succes " + data.buildingName)
        _state.update { it.copy(
            type = data.type,
            buildingName = data.buildingName,
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
            insIds = data.insIds,
        ) }
    }



    private fun fetchData() {
        screenModelScope.launch {


            val landDeferred = async { landRepository.getLand() }
            val insuranceDeferred = async { insuranceRepository.getInsurance() } // เรียกฟังก์ชันดึง Group

            // รอรับผลลัพธ์จากทั้ง 2 API
            val landResult = landDeferred.await()
            val insuranceResult = insuranceDeferred.await()


            landResult.onSuccess { landData ->
                _LandState.value = landData
                println("✅ Land Data: ${landData}")
            }.onFailure { error ->
                println("❌ Failed to get lands: ${error.message}")
            }


            insuranceResult.onSuccess { insuranceData ->
                _InsState.value = insuranceData
                println("✅ ins Data: $insuranceData")

            }.onFailure { error ->
                println("❌ Failed to get insurances: ${error.message}")
            }


        }
    }


    private val _addedAttachments = MutableStateFlow<List<Attachment>>(emptyList())
    private val _deleteAttachments = MutableStateFlow<List<Attachment>>(emptyList())

    private val _addedRef = MutableStateFlow<List<RefModel>>(emptyList())
    private val _deleteRef = MutableStateFlow<List<RefModel>>(emptyList())

    private val _addedIns = MutableStateFlow<List<InsRefModel>>(emptyList())
    private val _deleteIns = MutableStateFlow<List<InsRefModel>>(emptyList())






    fun updateAttachment(addedList: List<Attachment>, deletedList: List<Attachment>, addedRef:List<RefModel>, deletedRef:List<RefModel>, addedIns:List<InsRefModel>, deletedIns:List<InsRefModel>) {
        _addedAttachments.update { addedList }
        _deleteAttachments.update { deletedList }
        _addedRef.update { addedRef }
        _deleteRef.update { deletedRef }
        _addedIns.update { addedIns }
        _deleteIns.update { deletedIns }
    }

    private fun asRequest(): BuildingRequest {
        val current = _state.value



        // ✅ Map ข้อมูลให้มีทั้ง Byte, MimeType และ ชื่อไฟล์
        val allFiles = _addedAttachments.value.mapNotNull { attachment ->
            val bytes = attachment.platformData as? ByteArray ?: return@mapNotNull null

            // เช็กว่าเป็น PDF หรือ รูปภาพ
            val isPdf = attachment.name.endsWith(".pdf", ignoreCase = true) || attachment.type.toString().contains("PDF")
            val mimeType = if (isPdf) "application/pdf" else "image/jpeg"
            val extension = if (isPdf) "pdf" else "jpg"

            // ตั้งชื่อไฟล์ (เอา symbol มาต่อกับ index หรือเวลาเพื่อไม่ให้ซ้ำ)
            val fileName = "${current.buildingName}.$extension"

            BuildingFileUploadData(bytes = bytes, mimeType = mimeType, fileName = fileName)
        }

        return BuildingRequest(
            name = current.buildingName,

            area = current.area,
            amount = current.amount,
            description = current.description,
            locationAddress = current.locationAddress,
            locationSubDistrict = current.locationSubDistrict,
            locationDistrict = current.locationDistrict,
            locationProvince = current.locationProvince,
            locationPostalCode = current.locationPostalCode,
            files = allFiles,
            insIds = _addedIns.value.map { data ->
                InsReferenceData(
                    insName = data.insName,
                    insId = data.insId
                )
            },
            deleteInsListId = _deleteIns.value.map { data ->
                InsReferenceData(
                    insName = data.insName,
                    insId = data.insId
                )
            },
            referenceIds = _addedRef.value.map { data ->
                BuildingReferenceData(
                    areaName = data.areaName,
                    areaId = data.areaId
                )
            },
            deleteRefListId = _deleteRef.value.map {data ->
                BuildingReferenceData(
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

                val buildingResult = buildingRepository.updateBuilding(id,requestBody)

                // ดึงข้อมูลออกมาจาก Result Wrapper
                val buildingResponse = buildingResult.getOrNull()

                if (buildingResult.isSuccess && buildingResponse != null) {
                    // ✅ ดึง ID ที่ได้จาก API ของการสร้าง Land
                    // สมมติว่า field id อยู่ใน landResponse.data.id หรือตาม Model ของคุณ
                    val createdItemId = buildingResponse.id
                    println("✅ [ScreenModel] building Created ID: $createdItemId")

                }
                else {
                    println("❌ [ScreenModel] Create building Failed")
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
