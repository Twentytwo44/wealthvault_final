package com.wealthvault.financiallist.ui.debt

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.financiallist.usecase.FinanciallistUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// 🌟 Import Data Class
import com.wealthvault.liability_api.model.GetLiabilityData

class DebtScreenModel(
    private val useCase: FinanciallistUseCase
) : ScreenModel {

    // 🌟 แยกเก็บ 2 หมวด
    private val _loans = MutableStateFlow<List<GetLiabilityData>>(emptyList())
    val loans = _loans.asStateFlow()

    private val _expenses = MutableStateFlow<List<GetLiabilityData>>(emptyList())
    val expenses = _expenses.asStateFlow()

    fun fetchLiabilities() {
        screenModelScope.launch {
            useCase.getLiabilities()
                .onSuccess { allLiabilities ->
                    // 🌟 ดักฟังข้อมูลดิบที่ได้มาก่อนจะทำการ Filter
                    println("✅ [API SUCCESS] Liabilities Data (ทั้งหมด): $allLiabilities")

                    // 🌟 Filter ตาม Type
                    _loans.value = allLiabilities.filter { it.type == "LIABILITY_TYPE_LOAN" }
                    _expenses.value = allLiabilities.filter { it.type == "LIABILITY_TYPE_EXPENSE" }

                    println("🔍 กรองแล้วได้หนี้สิน (_loans): ${_loans.value.size} รายการ")
                    println("🔍 กรองแล้วได้รายจ่าย (_expenses): ${_expenses.value.size} รายการ")
                }
                .onFailure {
                    println("❌ [API ERROR] Liabilities พังเพราะ: ${it.message}")
                }
        }
    }
}