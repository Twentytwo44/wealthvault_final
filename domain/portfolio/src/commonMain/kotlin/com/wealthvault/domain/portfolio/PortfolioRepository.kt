package com.wealthvault.domain.portfolio

import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.architecture.CachedValue
import kotlinx.coroutines.flow.Flow

/**
 * Domain-facing portfolio contract. Implementations may use the legacy API
 * modules during migration, but callers never need to know that detail.
 */
interface PortfolioRepository {
    suspend fun getAccountById(id: String, force: Boolean = false): AppResult<BankAccountData>
    suspend fun getBuildingById(id: String, force: Boolean = false): AppResult<BuildingIdData>
    suspend fun getCashById(id: String, force: Boolean = false): AppResult<CashIdData>
    suspend fun getInsuranceById(id: String, force: Boolean = false): AppResult<InsuranceIdData>
    suspend fun getInvestmentById(id: String, force: Boolean = false): AppResult<InvestmentIdData>
    suspend fun getLandById(id: String, force: Boolean = false): AppResult<LandIdData>
    suspend fun getLiabilityById(id: String, force: Boolean = false): AppResult<LiabilityIdData>

    suspend fun getAccounts(force: Boolean = false): AppResult<List<AccountData>>
    suspend fun getCashes(force: Boolean = false): AppResult<List<GetCashData>>
    suspend fun getInvestments(force: Boolean = false): AppResult<List<GetInvestmentData>>
    suspend fun getInsurances(force: Boolean = false): AppResult<List<GetInsuranceData>>
    suspend fun getBuildings(force: Boolean = false): AppResult<List<GetBuildingData>>
    suspend fun getLands(force: Boolean = false): AppResult<List<GetLandData>>
    suspend fun getLiabilities(force: Boolean = false): AppResult<List<GetLiabilityData>>

    fun observeAccounts(): Flow<CachedValue<List<AccountData>>>
    fun observeCashes(): Flow<CachedValue<List<GetCashData>>>
    fun observeInvestments(): Flow<CachedValue<List<GetInvestmentData>>>
    fun observeInsurances(): Flow<CachedValue<List<GetInsuranceData>>>
    fun observeBuildings(): Flow<CachedValue<List<GetBuildingData>>>
    fun observeLands(): Flow<CachedValue<List<GetLandData>>>
    fun observeLiabilities(): Flow<CachedValue<List<GetLiabilityData>>>

    suspend fun refresh(force: Boolean = false): AppResult<Unit>

    suspend fun deleteAsset(id: String, type: String): AppResult<Boolean>
}
