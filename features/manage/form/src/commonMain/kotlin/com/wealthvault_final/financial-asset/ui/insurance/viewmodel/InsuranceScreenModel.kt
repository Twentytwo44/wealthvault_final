package com.wealthvault_final.`financial-asset`.ui.insurance.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import com.wealthvault_final.`financial-asset`.model.InsuranceModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class InsuranceScreenModel : ScreenModel {
    // 📦 ถังเก็บข้อมูล
    private val _state = MutableStateFlow(
        InsuranceModel(
            policyNumber = "",
            type = "",
            companyName = "",
            coverageAmount = 0.0,
            coveragePeriod = "",
            expDate = "",
            description = "",
            name = "",
            attachments = emptyList(),
            conDate = ""
        )
    )
    val state = _state.asStateFlow()

    // ✍️ ฟังก์ชันอัปเดตข้อมูลจากหน้าฟอร์ม
    fun updateForm(data: InsuranceModel) {
        println("data update succes " + data.name)
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
    }

}
