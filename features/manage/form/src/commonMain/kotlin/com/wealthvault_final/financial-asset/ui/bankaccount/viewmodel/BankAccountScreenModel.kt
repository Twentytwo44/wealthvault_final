package com.wealthvault_final.`financial-asset`.ui.bankaccount.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import com.wealthvault_final.`financial-asset`.model.BankAccountModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class BankAccountScreenModel : ScreenModel {
    // 📦 ถังเก็บข้อมูล
    private val _state = MutableStateFlow(
        BankAccountModel(
            type = "",
            bankName = "",
            bankId = "",
            name = "",
            amount = 0.0,
            description = "",
            attachments = emptyList()

        )
    )
    val state = _state.asStateFlow()

    // ✍️ ฟังก์ชันอัปเดตข้อมูลจากหน้าฟอร์ม
    fun updateForm(data: BankAccountModel) {
        println("data update succes " + data.name)
        _state.update { it.copy(name = data.name, type = data.type, bankName = data.bankName, bankId = data.bankId, amount = data.amount, description = data.description, attachments = data.attachments) }
    }

}
