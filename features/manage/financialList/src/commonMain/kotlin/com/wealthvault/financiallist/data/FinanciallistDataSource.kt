package com.wealthvault.financiallist.data

// 🌟 นำเข้า API ทั้ง 7 ตัวของคุณ Champ
import com.wealthvault.account_api.getaccount.GetAccountApi
import com.wealthvault.cash_api.getcash.GetCashApi
import com.wealthvault.investment_api.getinvestment.GetInvestmentApi
import com.wealthvault.insurance_api.getinsurance.GetInsuranceApi
import com.wealthvault.building_api.getbuilding.GetBuildingApi
import com.wealthvault.land_api.getland.GetLandApi
import com.wealthvault.liability_api.getliability.GetLiabilityApi

class FinanciallistDataSource(
    private val accountApi: GetAccountApi,
    private val cashApi: GetCashApi,
    private val investmentApi: GetInvestmentApi,
    private val insuranceApi: GetInsuranceApi,
    private val buildingApi: GetBuildingApi,
    private val landApi: GetLandApi,
    private val liabilityApi: GetLiabilityApi
) {
    suspend fun getAccount() = accountApi.getAccount()
    suspend fun getCash() = cashApi.getCash()
    suspend fun getInvestment() = investmentApi.getInvestment()
    suspend fun getInsurance() = insuranceApi.getInsurance()
    suspend fun getBuilding() = buildingApi.getBuilding()
    suspend fun getLand() = landApi.getLand()
    suspend fun getLiability() = liabilityApi.getLiability()
}