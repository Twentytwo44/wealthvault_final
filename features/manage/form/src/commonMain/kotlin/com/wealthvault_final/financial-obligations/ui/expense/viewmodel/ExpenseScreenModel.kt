package com.wealthvault_final.`financial-obligations`.ui.expense.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import com.wealthvault_final.`financial-obligations`.model.ExpenseModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ExpenseScreenModel : ScreenModel {
    // 📦 ถังเก็บข้อมูล
    private val _state = MutableStateFlow(
        ExpenseModel(
            type = "",
            name = "",
            principal = 0.0,
            interestRate = "",
            description = "",
            startedAt = "",
            endedAt = "",
            creditor = "",
            attachments = emptyList()

        )
    )
    val state = _state.asStateFlow()

    // ✍️ ฟังก์ชันอัปเดตข้อมูลจากหน้าฟอร์ม
    fun updateForm(data: ExpenseModel) {
        println("data update succes " + data.name)
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
    }

}
