package com.wealthvault_final.`financial-asset`.ui.realestate.building.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.insurance_api.model.GetInsuranceData
import com.wealthvault.land_api.model.GetLandData
import com.wealthvault_final.`financial-asset`.data.insurance.GetInsuranceRepositoryImpl
import com.wealthvault_final.`financial-asset`.data.land.GetLandRepositoryImpl
import com.wealthvault_final.`financial-asset`.model.BuildingModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BuildingScreenModel(
    private val landRepository: GetLandRepositoryImpl,
    private val insuranceRepository: GetInsuranceRepositoryImpl,
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

}
