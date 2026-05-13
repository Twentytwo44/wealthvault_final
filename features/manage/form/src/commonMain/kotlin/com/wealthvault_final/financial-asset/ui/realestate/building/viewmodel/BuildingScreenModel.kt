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
import kotlinx.coroutines.launch

class BuildingScreenModel(
    private val landRepository: GetLandRepositoryImpl,
    private val insuranceRepository: GetInsuranceRepositoryImpl,
) : ScreenModel {

    // 🌟 State สำหรับเก็บข้อมูลที่ดินอ้างอิง
    private val _LandState = MutableStateFlow<List<GetLandData>>(emptyList())
    val LandState = _LandState.asStateFlow()

    // 🌟 State สำหรับเก็บข้อมูลประกันอ้างอิง
    private val _InsState = MutableStateFlow<List<GetInsuranceData>>(emptyList())
    val InsState = _InsState.asStateFlow()

    // 🌟 State สำหรับเก็บข้อมูลฟอร์มของอาคาร
    private val _state = MutableStateFlow(
        BuildingModel(
            type = "",
            buildingName = "",
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
            insIds = emptyList()
        )
    )
    val state = _state.asStateFlow()


    // ✍️ ฟังก์ชันอัปเดตข้อมูลจากหน้าฟอร์ม
    fun updateForm(data: BuildingModel) {
        println("✅ [ViewModel] Data updated success: ${data.buildingName}")

        // 🌟 ทริค: ไม่ต้องใช้ it.copy() แมปทีละตัวแล้ว โยนก้อน Data ใหม่ทับลงไปได้เลย
        // โค้ดสั้นลง และป้องกันบั๊กเวลามีการเพิ่ม/ลด ฟิลด์ในอนาคตครับ
        _state.value = data
    }

    fun fetchData() {
        screenModelScope.launch {
            try {
                // 🚀 ยิง API พร้อมกัน 2 ตัวเพื่อความรวดเร็ว
                val landDeferred = async { landRepository.getLand() }
                val insuranceDeferred = async { insuranceRepository.getInsurance() }

                // รอรับผลลัพธ์จากทั้ง 2 API
                val landResult = landDeferred.await()
                val insuranceResult = insuranceDeferred.await()

                // 📌 จัดการผลลัพธ์ของข้อมูลที่ดิน
                landResult.onSuccess { landData ->
                    _LandState.value = landData
                    println("✅ [ViewModel] Land Data Loaded: ${landData.size} items")
                }.onFailure { error ->
                    println("❌ [ViewModel] Failed to get lands: ${error.message}")
                }

                // 📌 จัดการผลลัพธ์ของข้อมูลประกัน
                insuranceResult.onSuccess { insuranceData ->
                    _InsState.value = insuranceData
                    println("✅ [ViewModel] Insurance Data Loaded: ${insuranceData.size} items")
                }.onFailure { error ->
                    println("❌ [ViewModel] Failed to get insurances: ${error.message}")
                }

            } catch (e: Exception) {
                // 🌟 ดักจับ Error ก้อนใหญ่เผื่อมีปัญหาตอนทำ async/await
                println("❌ [ViewModel] Fetch Data Exception: ${e.message}")
            }
        }
    }
}
