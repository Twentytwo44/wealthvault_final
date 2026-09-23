package com.wealthvault.`financial-asset`.ui.insurance.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import com.wealthvault.domain.portfolio.InsuranceModel
import com.wealthvault.core.model.Money
import com.wealthvault.core.architecture.FormAction
import com.wealthvault.core.architecture.UiState
import com.wealthvault.core.architecture.UiStateHolder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class InsuranceScreenModel : ScreenModel {
    // 📦 ถังเก็บข้อมูล
    private val _state = MutableStateFlow(
        InsuranceModel(
            policyNumber = "",
            type = "",
            companyName = "",
            coverageAmount = Money(0),
            coveragePeriod = "",
            expDate = "",
            description = "",
            name = "",
            attachments = emptyList(),
            conDate = ""
        )
    )
    val state = _state.asStateFlow()
    private val udf = UiStateHolder(_state.value)
    val uiState: StateFlow<UiState<InsuranceModel>> = udf.state
    val effects = udf.effects

    fun onAction(action: FormAction<InsuranceModel>) {
        when (action) {
            is FormAction.Changed -> updateForm(action.value)
            is FormAction.AttachmentsChanged -> updateForm(_state.value.copy(attachments = action.added))
            is FormAction.Submit -> Unit
        }
    }

    // ✍️ ฟังก์ชันอัปเดตข้อมูลจากหน้าฟอร์ม
    fun updateForm(data: InsuranceModel) {
        _state.update { it.copy(
            policyNumber = data.policyNumber,
            type = data.type,
            companyName = data.companyName,
            coverageAmount = data.coverageAmount,
            coveragePeriod = data.coveragePeriod,
            expDate = data.expDate,
            description = data.description,
            name = data.name,
            attachments = data.attachments
        ) }
        udf.set(_state.value, isLoading = false, error = null)
    }

}
