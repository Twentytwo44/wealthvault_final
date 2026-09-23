package com.wealthvault.`financial-obligations`.ui.liability.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import com.wealthvault.domain.portfolio.LiabilityModel
import com.wealthvault.core.model.Money
import com.wealthvault.core.architecture.FormAction
import com.wealthvault.core.architecture.UiState
import com.wealthvault.core.architecture.UiStateHolder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LiabilityScreenModel : ScreenModel {
    // 📦 ถังเก็บข้อมูล
    private val _state = MutableStateFlow(
        LiabilityModel(
            type = "",
            name = "",
            principal = Money(0),
            interestRate = "",
            description = "",
            startedAt = "",
            endedAt = "",
            creditor = "",
            attachments = emptyList()

        )
    )
    val state = _state.asStateFlow()
    private val udf = UiStateHolder(_state.value)
    val uiState: StateFlow<UiState<LiabilityModel>> = udf.state
    val effects = udf.effects

    fun onAction(action: FormAction<LiabilityModel>) {
        when (action) {
            is FormAction.Changed -> updateForm(action.value)
            is FormAction.AttachmentsChanged -> updateForm(_state.value.copy(attachments = action.added))
            is FormAction.Submit -> Unit
        }
    }

    // ✍️ ฟังก์ชันอัปเดตข้อมูลจากหน้าฟอร์ม
    fun updateForm(data: LiabilityModel) {
        _state.update { it.copy(
            name = data.name,
            type = data.type,
            principal = data.principal,
            interestRate = data.interestRate,
            description = data.description,
            startedAt = data.startedAt,
            endedAt = data.endedAt,
            creditor = data.creditor,
            attachments = data.attachments

        ) }
        udf.set(_state.value, isLoading = false, error = null)
    }

}
