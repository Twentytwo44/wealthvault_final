package com.wealthvault.`financial-asset`.ui.realestate.land.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.domain.portfolio.BuildingReferenceRepository
import com.wealthvault.domain.portfolio.LandModel
import com.wealthvault.domain.portfolio.GetBuildingData
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LandScreenModel(
    private val buildingRepository: BuildingReferenceRepository,
) : ScreenModel {
    // 📦 ถังเก็บข้อมูล
    private val _state = MutableStateFlow(

        LandModel(
            deedNum = "",
            landName = "",
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

        )
    )
    val state = _state.asStateFlow()
    private val udf = UiStateHolder(_state.value)
    val uiState: StateFlow<UiState<LandModel>> = udf.state
    val effects = udf.effects
    private var fetchJob: kotlinx.coroutines.Job? = null

    fun onAction(action: FormAction<LandModel>) {
        when (action) {
            is FormAction.Changed -> updateForm(action.value)
            is FormAction.AttachmentsChanged -> updateForm(_state.value.copy(attachments = action.added))
            is FormAction.Submit -> Unit
        }
    }

    private val _BuildingState = MutableStateFlow<List<GetBuildingData>>(emptyList())
    val BuildingState = _BuildingState.asStateFlow()


    // ✍️ ฟังก์ชันอัปเดตข้อมูลจากหน้าฟอร์ม
    fun updateForm(data: LandModel) {
        _state.update { it.copy(
            deedNum = data.deedNum,
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
        udf.set(_state.value, isLoading = false, error = null)
    }

    private fun fetchData() {
        if (fetchJob?.isActive == true) return
        udf.loading()
        val job = screenModelScope.launch {
            try {
                val buildingResult = async { buildingRepository.getBuilding() }.await()
                buildingResult.onSuccess {
                    _BuildingState.value = it
                    udf.success(_state.value)
                }.onFailure { udf.failure(it.toAppError()) }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                udf.failure(error.toAppError())
            }
        }
        fetchJob = job
        job.invokeOnCompletion { if (fetchJob === job) fetchJob = null }
    }

}
