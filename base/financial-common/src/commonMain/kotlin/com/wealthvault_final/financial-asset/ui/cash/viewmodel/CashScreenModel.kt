package com.wealthvault.`financial-asset`.ui.cash.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import com.wealthvault.domain.portfolio.CashModel
import com.wealthvault.core.model.Money
import com.wealthvault.core.architecture.FormAction
import com.wealthvault.core.architecture.UiState
import com.wealthvault.core.architecture.UiStateHolder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CashScreenModel : ScreenModel {
    // 📦 ถังเก็บข้อมูล
    private val _state = MutableStateFlow(
        CashModel(
            cashName = "",
            amount = Money(0),
            description = "",
            attachments = emptyList()
        )
    )
    val state = _state.asStateFlow()
    private val udf = UiStateHolder(_state.value)
    val uiState: StateFlow<UiState<CashModel>> = udf.state
    val effects = udf.effects

    fun onAction(action: FormAction<CashModel>) {
        when (action) {
            is FormAction.Changed -> updateForm(action.value)
            is FormAction.AttachmentsChanged -> updateForm(_state.value.copy(attachments = action.added))
            is FormAction.Submit -> Unit
        }
    }

    // ✍️ ฟังก์ชันอัปเดตข้อมูลจากหน้าฟอร์ม
    fun updateForm(data: CashModel) {
        _state.update { it.copy(cashName = data.cashName, amount = data.amount, description = data.description, attachments = data.attachments) }
        udf.set(_state.value, isLoading = false, error = null)
    }

}
