package com.wealthvault.data.portfolio.repository

import com.wealthvault.domain.portfolio.AccountData
import com.wealthvault.domain.portfolio.BankAccountData
import com.wealthvault.domain.portfolio.BuildingIdData
import com.wealthvault.domain.portfolio.CashIdData
import com.wealthvault.domain.portfolio.GetBuildingData
import com.wealthvault.domain.portfolio.GetCashData
import com.wealthvault.domain.portfolio.GetInsuranceData
import com.wealthvault.domain.portfolio.GetInvestmentData
import com.wealthvault.domain.portfolio.GetLandData
import com.wealthvault.domain.portfolio.GetLiabilityData
import com.wealthvault.domain.portfolio.InsuranceIdData
import com.wealthvault.domain.portfolio.InvestmentIdData
import com.wealthvault.domain.portfolio.LandIdData
import com.wealthvault.domain.portfolio.LiabilityIdData

/**
 * Domain-shaped read/delete boundary for the portfolio data implementation.
 *
 * Endpoint interfaces remain wiring details of [FinanciallistDataSource]; the
 * repository depends on this contract so cache and repository tests do not
 * need to construct a graph of HTTP endpoint implementations.
 */
interface FinanciallistRemoteDataSource {
    suspend fun getAccount(): List<AccountData>
    suspend fun getCash(): List<GetCashData>
    suspend fun getInvestment(): List<GetInvestmentData>
    suspend fun getInsurance(): List<GetInsuranceData>
    suspend fun getBuilding(): List<GetBuildingData>
    suspend fun getLand(): List<GetLandData>
    suspend fun getLiability(): List<GetLiabilityData>

    suspend fun getAccountById(id: String): BankAccountData?
    suspend fun getBuildingById(id: String): BuildingIdData?
    suspend fun getCashById(id: String): CashIdData?
    suspend fun getInsuranceById(id: String): InsuranceIdData?
    suspend fun getInvestmentById(id: String): InvestmentIdData?
    suspend fun getLandById(id: String): LandIdData?
    suspend fun getLiabilityById(id: String): LiabilityIdData?

    /**
     * Deletes one portfolio item and returns the server-confirmed outcome.
     *
     * The wire response is intentionally consumed by the transport adapter;
     * no API DTO crosses this data boundary.
     */
    suspend fun deleteAsset(id: String, type: String): Boolean
}
