package com.wealthvault.financiallist.usecase

import com.wealthvault.financiallist.data.FinanciallistRepositoryImpl
import com.wealthvault.account_api.model.AccountData
import com.wealthvault.cash_api.model.GetCashData
import com.wealthvault.investment_api.model.GetInvestmentData
import com.wealthvault.insurance_api.model.GetInsuranceData
import com.wealthvault.building_api.model.GetBuildingData
import com.wealthvault.land_api.model.GetLandData
import com.wealthvault.liability_api.model.GetLiabilityData

class FinanciallistUseCase(
    private val repository: FinanciallistRepositoryImpl
) {
    // 🌟 ต้องมี Result<List<...>> ต่อท้ายทุกบรรทัด
    suspend fun getAccounts(): Result<List<AccountData>> = repository.getAccounts()
    suspend fun getCashes(): Result<List<GetCashData>> = repository.getCashes()
    suspend fun getInvestments(): Result<List<GetInvestmentData>> = repository.getInvestments()
    suspend fun getInsurances(): Result<List<GetInsuranceData>> = repository.getInsurances()
    suspend fun getBuildings(): Result<List<GetBuildingData>> = repository.getBuildings()
    suspend fun getLands(): Result<List<GetLandData>> = repository.getLands()
    suspend fun getLiabilities(): Result<List<GetLiabilityData>> = repository.getLiabilities()
    suspend fun getAccountById(id: String) = repository.getAccountById(id)
    suspend fun getBuildingById(id: String) = repository.getBuildingById(id)
    suspend fun getCashById(id: String) = repository.getCashById(id)
    suspend fun getInsuranceById(id: String) = repository.getInsuranceById(id)
    suspend fun getInvestmentById(id: String) = repository.getInvestmentById(id)
    suspend fun getLandById(id: String) = repository.getLandById(id)
    suspend fun getLiabilityById(id: String) = repository.getLiabilityById(id)


}