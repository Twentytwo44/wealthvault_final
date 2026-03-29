package com.wealthvault.financiallist.ui.asset

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.financiallist.usecase.FinanciallistUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import com.wealthvault.account_api.model.AccountData
import com.wealthvault.cash_api.model.GetCashData
import com.wealthvault.investment_api.model.GetInvestmentData
import com.wealthvault.insurance_api.model.GetInsuranceData
import com.wealthvault.building_api.model.GetBuildingData
import com.wealthvault.land_api.model.GetLandData

class AssetScreenModel(
    private val useCase: FinanciallistUseCase
) : ScreenModel {

    private val _accounts = MutableStateFlow<List<AccountData>>(emptyList())
    val accounts = _accounts.asStateFlow()

    private val _cashes = MutableStateFlow<List<GetCashData>>(emptyList())
    val cashes = _cashes.asStateFlow()

    private val _investments = MutableStateFlow<List<GetInvestmentData>>(emptyList())
    val investments = _investments.asStateFlow()

    private val _insurances = MutableStateFlow<List<GetInsuranceData>>(emptyList())
    val insurances = _insurances.asStateFlow()

    private val _buildings = MutableStateFlow<List<GetBuildingData>>(emptyList())
    val buildings = _buildings.asStateFlow()

    private val _lands = MutableStateFlow<List<GetLandData>>(emptyList())
    val lands = _lands.asStateFlow()

    fun fetchAllAssets() {
        screenModelScope.launch {
            // 🌟 1. บัญชีเงินฝาก
            useCase.getAccounts()
                .onSuccess {
                    println("✅ [API SUCCESS] Accounts Data: $it")
                    _accounts.value = it
                }
                .onFailure { println("❌ [API ERROR] Accounts พังเพราะ: ${it.message}") }

            // 🌟 2. เงินสด
            useCase.getCashes()
                .onSuccess {
                    println("✅ [API SUCCESS] Cashes Data: $it")
                    _cashes.value = it
                }
                .onFailure { println("❌ [API ERROR] Cashes พังเพราะ: ${it.message}") }

            // 🌟 3. การลงทุน
            useCase.getInvestments()
                .onSuccess {
                    println("✅ [API SUCCESS] Investments Data: $it")
                    _investments.value = it
                }
                .onFailure { println("❌ [API ERROR] Investments พังเพราะ: ${it.message}") }

            // 🌟 4. ประกัน
            useCase.getInsurances()
                .onSuccess {
                    println("✅ [API SUCCESS] Insurances Data: $it")
                    _insurances.value = it
                }
                .onFailure { println("❌ [API ERROR] Insurances พังเพราะ: ${it.message}") }

            // 🌟 5. บ้าน/อาคาร
            useCase.getBuildings()
                .onSuccess {
                    println("✅ [API SUCCESS] Buildings Data: $it")
                    _buildings.value = it
                }
                .onFailure { println("❌ [API ERROR] Buildings พังเพราะ: ${it.message}") }

            // 🌟 6. ที่ดิน
            useCase.getLands()
                .onSuccess {
                    println("✅ [API SUCCESS] Lands Data: $it")
                    _lands.value = it
                }
                .onFailure { println("❌ [API ERROR] Lands พังเพราะ: ${it.message}") }
        }
    }
}