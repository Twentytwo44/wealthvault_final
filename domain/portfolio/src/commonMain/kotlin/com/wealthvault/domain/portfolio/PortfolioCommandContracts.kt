package com.wealthvault.domain.portfolio

import com.wealthvault.core.architecture.AppResult

/**
 * Command contracts shared by the portfolio forms and their data
 * implementations.  AppResult keeps transport exceptions out of the
 * presentation/domain boundary while the legacy API adapters remain private
 * to data modules.
 */
interface CreateBankAccountRepository {
    suspend fun createBankAccount(request: BankAccountRequest): AppResult<BankAccountData>
}

interface CreateBuildingRepository {
    suspend fun createBuilding(request: BuildingRequest): AppResult<BuildingData>
}

interface BuildingReferenceRepository {
    suspend fun getBuilding(force: Boolean = false): AppResult<List<GetBuildingData>>
}

interface CreateCashRepository {
    suspend fun create(request: CashRequest): AppResult<CashData>
}

interface CreateInsuranceRepository {
    suspend fun createInsurance(request: InsuranceRequest): AppResult<InsuranceData>
}

interface InsuranceReferenceRepository {
    suspend fun getInsurance(force: Boolean = false): AppResult<List<GetInsuranceData>>
}

interface CreateInvestmentRepository {
    suspend fun create(request: InvestmentRequest): AppResult<InvestmentData>
}

interface CreateLandRepository {
    suspend fun create(request: LandRequest): AppResult<LandData>
}

interface LandReferenceRepository {
    suspend fun getLand(force: Boolean = false): AppResult<List<GetLandData>>
}

interface CreateLiabilityRepository {
    suspend fun createLiability(request: LiabilityRequest): AppResult<LiabilityData>
}

/** Update contracts used by the newer financial-list forms. */
interface UpdateBankAccountRepository {
    suspend fun updateAccount(id: String, request: BankAccountRequest): AppResult<BankAccountData>
}

interface UpdateBuildingRepository {
    suspend fun updateBuilding(id: String, request: BuildingRequest): AppResult<BuildingData>
}

interface UpdateCashRepository {
    suspend fun updateCash(id: String, request: CashRequest): AppResult<CashData>
}

interface UpdateInsuranceRepository {
    suspend fun updateInsurance(id: String, request: InsuranceRequest): AppResult<InsuranceData>
}

interface UpdateInvestmentRepository {
    suspend fun updateInvestment(id: String, request: InvestmentRequest): AppResult<InvestmentData>
}

interface UpdateLandRepository {
    suspend fun updateLand(id: String, request: LandRequest): AppResult<LandData>
}

interface UpdateLiabilityRepository {
    suspend fun updateLiability(id: String, request: LiabilityRequest): AppResult<LiabilityData>
}
