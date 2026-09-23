package com.wealthvault.`financial-asset`.ui.realestate.building.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.domain.portfolio.InsuranceReferenceRepository
import com.wealthvault.domain.portfolio.LandReferenceRepository
import com.wealthvault.domain.portfolio.BuildingModel
import com.wealthvault.domain.portfolio.GetInsuranceData
import com.wealthvault.domain.portfolio.GetLandData
import com.wealthvault.core.model.Money
import com.wealthvault.core.architecture.FormAction
import com.wealthvault.core.architecture.UiState
import com.wealthvault.core.architecture.UiStateHolder
import com.wealthvault.core.architecture.toAppError
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BuildingScreenModel(
    private val landRepository: LandReferenceRepository,
    private val insuranceRepository: InsuranceReferenceRepository,
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
            amount = Money(0),
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
    private val udf = UiStateHolder(_state.value)
    val uiState: StateFlow<UiState<BuildingModel>> = udf.state
    val effects = udf.effects
    private var fetchJob: kotlinx.coroutines.Job? = null

    fun onAction(action: FormAction<BuildingModel>) {
        when (action) {
            is FormAction.Changed -> updateForm(action.value)
            is FormAction.AttachmentsChanged -> updateForm(_state.value.copy(attachments = action.added))
            is FormAction.Submit -> Unit
        }
    }


    // ✍️ ฟังก์ชันอัปเดตข้อมูลจากหน้าฟอร์ม
    fun updateForm(data: BuildingModel) {

        // 🌟 ทริค: ไม่ต้องใช้ it.copy() แมปทีละตัวแล้ว โยนก้อน Data ใหม่ทับลงไปได้เลย
        // โค้ดสั้นลง และป้องกันบั๊กเวลามีการเพิ่ม/ลด ฟิลด์ในอนาคตครับ
        _state.value = data
        udf.set(_state.value, isLoading = false, error = null)
    }

    fun fetchData() {
        if (fetchJob?.isActive == true) return
        udf.loading()
        val job = screenModelScope.launch {
            try {
                val landDeferred = async { landRepository.getLand() }
                val insuranceDeferred = async { insuranceRepository.getInsurance() }
                val landResult = landDeferred.await()
                val insuranceResult = insuranceDeferred.await()
                var failure: com.wealthvault.core.architecture.AppError? = null
                landResult.onSuccess { _LandState.value = it }
                    .onFailure { failure = it.toAppError() }
                insuranceResult.onSuccess { _InsState.value = it }
                    .onFailure { if (failure == null) failure = it.toAppError() }
                failure?.let { udf.failure(it) } ?: udf.success(_state.value)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Throwable) {
                udf.failure(e.toAppError())
            }
        }
        fetchJob = job
        job.invokeOnCompletion { if (fetchJob === job) fetchJob = null }
    }
}
