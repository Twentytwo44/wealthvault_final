package com.wealthvault_final.`financial-asset`.ui.cash.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import com.wealthvault_final.`financial-asset`.model.CashModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CashScreenModel : ScreenModel {
    // 📦 ถังเก็บข้อมูล
    private val _state = MutableStateFlow(
        CashModel(
            cashName = "",
            amount = 0.0,
            description = "",
            attachments = emptyList()
        )
    )
    val state = _state.asStateFlow()

    // ✍️ ฟังก์ชันอัปเดตข้อมูลจากหน้าฟอร์ม
    fun updateForm(data: CashModel) {
        println("data update succes " + data.cashName)
        _state.update { it.copy(cashName = data.cashName, amount = data.amount, description = data.description, attachments = data.attachments) }
    }

}
