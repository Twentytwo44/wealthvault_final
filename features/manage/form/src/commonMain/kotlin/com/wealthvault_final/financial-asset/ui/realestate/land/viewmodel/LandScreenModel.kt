package com.wealthvault_final.`financial-asset`.ui.realestate.land.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.building_api.model.GetBuildingData
import com.wealthvault_final.`financial-asset`.data.building.GetBuildingRepositoryImpl
import com.wealthvault_final.`financial-asset`.model.LandModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LandScreenModel(
    private val buildingRepository: GetBuildingRepositoryImpl,
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

    init {
        fetchData()
    }

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

    private fun fetchData() {
        screenModelScope.launch {


            val buildingDeferred = async { buildingRepository.getBuilding() }

            // รอรับผลลัพธ์จากทั้ง 2 API
            val buildingResult = buildingDeferred.await()


            buildingResult.onSuccess { buildingData ->
                _BuildingState.value = buildingData
                println("✅ Land Data: ${buildingData}")
            }.onFailure { error ->
                println("❌ Failed to get lands: ${error.message}")
            }



        }
    }

}
