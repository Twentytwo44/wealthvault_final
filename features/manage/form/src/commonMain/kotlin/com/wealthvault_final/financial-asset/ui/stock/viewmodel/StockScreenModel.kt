package com.wealthvault_final.`financial-asset`.ui.stock.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import com.wealthvault_final.`financial-asset`.model.StockModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class StockScreenModel : ScreenModel {
    // 📦 ถังเก็บข้อมูล
    private val _state = MutableStateFlow(StockModel(
        stockName = "",
        quantity = 0.00,
        description = "",
        stockSymbol = "",
        brokerName = "",
        costPerPrice = 0.00,
        attachments = emptyList(),
        type = ""
    ))
    val state = _state.asStateFlow()

    // ✍️ ฟังก์ชันอัปเดตข้อมูลจากหน้าฟอร์ม
    fun updateForm(data: StockModel) {
        println("data update succes " + data.stockName)
        _state.update { it.copy(stockName = data.stockName, quantity = data.quantity, description = data.description, stockSymbol = data.stockSymbol, brokerName = data.brokerName, costPerPrice = data.costPerPrice, attachments = data.attachments,type = data.type) }
    }

}
