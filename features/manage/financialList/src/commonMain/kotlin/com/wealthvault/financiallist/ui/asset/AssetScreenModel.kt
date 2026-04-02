package com.wealthvault.financiallist.ui.asset

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.financiallist.usecase.FinanciallistUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import com.wealthvault.account_api.model.AccountData
import com.wealthvault.account_api.model.BankAccountData // 🌟 นำเข้า BankAccountData
import com.wealthvault.cash_api.model.GetCashData
import com.wealthvault.investment_api.model.GetInvestmentData
import com.wealthvault.insurance_api.model.GetInsuranceData
import com.wealthvault.building_api.model.GetBuildingData
import com.wealthvault.land_api.model.GetLandData
import com.wealthvault.building_api.model.BuildingIdData
import com.wealthvault.cash_api.model.CashIdData
import com.wealthvault.insurance_api.model.InsuranceIdData
import com.wealthvault.investment_api.model.InvestmentIdData
import com.wealthvault.land_api.model.LandIdData

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
            useCase.getAccounts().onSuccess { _accounts.value = it }
            useCase.getCashes().onSuccess { _cashes.value = it }
            useCase.getInvestments().onSuccess { _investments.value = it }
            useCase.getInsurances().onSuccess { _insurances.value = it }
            useCase.getBuildings().onSuccess { _buildings.value = it }
            useCase.getLands().onSuccess { _lands.value = it }
        }
    }

    // 🌟 เปลี่ยนมาเรียกใช้ useCase แทน repository
    suspend fun getAccountById(id: String): BankAccountData? {
        return useCase.getAccountById(id) // ⚠️ อย่าลืมไปเพิ่มฟังก์ชันนี้ใน FinanciallistUseCase ด้วยนะครับ
            .onSuccess { println("✅ โหลดบัญชีสำเร็จ: ${it.name}") }
            .onFailure { println("🚨 โหลดล้มเหลว: ${it.message}") }
            .getOrNull()
    }
    suspend fun getBuildingById(id: String): BuildingIdData? {
        return useCase.getBuildingById(id)
            .onSuccess { println("✅ โหลด Building สำเร็จ: ${it.name}") }
            .onFailure { println("🚨 โหลด Building ล้มเหลว: ${it.message}") }
            .getOrNull()
    }
    suspend fun getCashById(id: String): CashIdData? {
        return useCase.getCashById(id)
            .onSuccess { println("✅ โหลด Cash สำเร็จ: ${it.name}") }
            .onFailure { println("🚨 โหลด Cash ล้มเหลว: ${it.message}") }
            .getOrNull()
    }
    suspend fun getInsuranceById(id: String): InsuranceIdData? {
        return useCase.getInsuranceById(id).getOrNull()
    }

    suspend fun getInvestmentById(id: String): InvestmentIdData? {
        return useCase.getInvestmentById(id).getOrNull()
    }

    suspend fun getLandById(id: String): LandIdData? {
        return useCase.getLandById(id).getOrNull()
    }
    // ในไฟล์ AssetScreenModel.kt

    fun deleteAsset(id: String, type: String) {
        screenModelScope.launch {
            // 1. สั่งลบผ่าน UseCase
            val result = useCase.deleteAsset(id, type)

            result.onSuccess {
                println("✅ ลบ $type สำเร็จ!")

                // 🌟 2. จุดสำคัญ: ต้องเรียกฟังก์ชันนี้เพื่อให้มันไปดึงข้อมูลใหม่จาก API มาใส่ StateFlow
                fetchAllAssets()

            }.onFailure { error ->
                println("🚨 ลบ $type ล้มเหลว: ${error.message}")
            }
        }
    }
}