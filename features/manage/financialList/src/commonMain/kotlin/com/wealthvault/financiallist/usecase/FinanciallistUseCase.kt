package com.wealthvault.financiallist.usecase

import com.wealthvault.domain.portfolio.AccountData
import com.wealthvault.domain.portfolio.GetCashData
import com.wealthvault.domain.portfolio.GetInvestmentData
import com.wealthvault.domain.portfolio.GetInsuranceData
import com.wealthvault.domain.portfolio.GetBuildingData
import com.wealthvault.domain.portfolio.GetLandData
import com.wealthvault.domain.portfolio.GetLiabilityData
import com.wealthvault.core.architecture.AppResult
import com.wealthvault.domain.portfolio.PortfolioRepository

class FinanciallistUseCase(
    private val repository: PortfolioRepository
) {
    // --- Get All Lists ---
    suspend fun getAccounts(force: Boolean = false): AppResult<List<AccountData>> = repository.getAccounts(force)
    suspend fun getCashes(force: Boolean = false): AppResult<List<GetCashData>> = repository.getCashes(force)
    suspend fun getInvestments(force: Boolean = false): AppResult<List<GetInvestmentData>> = repository.getInvestments(force)
    suspend fun getInsurances(force: Boolean = false): AppResult<List<GetInsuranceData>> = repository.getInsurances(force)
    suspend fun getBuildings(force: Boolean = false): AppResult<List<GetBuildingData>> = repository.getBuildings(force)
    suspend fun getLands(force: Boolean = false): AppResult<List<GetLandData>> = repository.getLands(force)
    suspend fun getLiabilities(force: Boolean = false): AppResult<List<GetLiabilityData>> = repository.getLiabilities(force)

    // --- Get By ID ---
    suspend fun getAccountById(id: String, force: Boolean = false) = repository.getAccountById(id, force)
    suspend fun getBuildingById(id: String, force: Boolean = false) = repository.getBuildingById(id, force)
    suspend fun getCashById(id: String, force: Boolean = false) = repository.getCashById(id, force)
    suspend fun getInsuranceById(id: String, force: Boolean = false) = repository.getInsuranceById(id, force)
    suspend fun getInvestmentById(id: String, force: Boolean = false) = repository.getInvestmentById(id, force)
    suspend fun getLandById(id: String, force: Boolean = false) = repository.getLandById(id, force)
    suspend fun getLiabilityById(id: String, force: Boolean = false) = repository.getLiabilityById(id, force)

    // 🌟 --- Delete Asset (ตัวที่ขาดไปครับ!) ---
    suspend fun deleteAsset(id: String, type: String): AppResult<Boolean> {
        return repository.deleteAsset(id, type)
    }
}
