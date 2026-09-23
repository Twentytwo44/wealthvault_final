package com.wealthvault.`financial-asset`.ui.stock.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import com.wealthvault.domain.portfolio.StockModel
import com.wealthvault.core.model.FixedDecimal
import com.wealthvault.core.model.Money
import com.wealthvault.core.architecture.FormAction
import com.wealthvault.core.architecture.UiState
import com.wealthvault.core.architecture.UiStateHolder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class StockScreenModel : ScreenModel {
    // 📦 ถังเก็บข้อมูล
    private val _state = MutableStateFlow(StockModel(
        stockName = "",
        quantity = FixedDecimal(0, 4),
        description = "",
        stockSymbol = "",
        brokerName = "",
        costPerPrice = Money(0),
        attachments = emptyList(),
        type = ""
    ))
    val state = _state.asStateFlow()
    private val udf = UiStateHolder(_state.value)
    val uiState: StateFlow<UiState<StockModel>> = udf.state
    val effects = udf.effects

    fun onAction(action: FormAction<StockModel>) {
        when (action) {
            is FormAction.Changed -> updateForm(action.value)
            is FormAction.AttachmentsChanged -> updateForm(_state.value.copy(attachments = action.added))
            is FormAction.Submit -> Unit
        }
    }

    // ✍️ ฟังก์ชันอัปเดตข้อมูลจากหน้าฟอร์ม
    fun updateForm(data: StockModel) {
        _state.update { it.copy(stockName = data.stockName, quantity = data.quantity, description = data.description, stockSymbol = data.stockSymbol, brokerName = data.brokerName, costPerPrice = data.costPerPrice, attachments = data.attachments,type = data.type) }
        udf.set(_state.value, isLoading = false, error = null)
    }

}
