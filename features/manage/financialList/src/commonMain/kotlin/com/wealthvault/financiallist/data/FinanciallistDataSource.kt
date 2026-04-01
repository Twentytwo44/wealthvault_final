package com.wealthvault.financiallist.data

// 🌟 นำเข้า API ทั้ง 7 ตัว + ตัว Get By ID ของ Account
import com.wealthvault.account_api.getaccount.GetAccountApi
import com.wealthvault.account_api.getaccountbyid.GetAccountByIdApi // 🌟 เพิ่มบรรทัดนี้
import com.wealthvault.cash_api.getcash.GetCashApi
import com.wealthvault.investment_api.getinvestment.GetInvestmentApi
import com.wealthvault.insurance_api.getinsurance.GetInsuranceApi
import com.wealthvault.building_api.getbuilding.GetBuildingApi
import com.wealthvault.building_api.getbuildingbyid.GetBuildingByIdApi
import com.wealthvault.cash_api.getcashtbyid.GetCashByIdApi
import com.wealthvault.insurance_api.getinsurancetbyid.GetInsuranceByIdApi
import com.wealthvault.investment_api.getinvestmentbyid.GetInvestmentByIdApi
import com.wealthvault.land_api.getland.GetLandApi
import com.wealthvault.land_api.getlandbyid.GetLandByIdApi
import com.wealthvault.liability_api.getliability.GetLiabilityApi
import com.wealthvault.liability_api.getliabilitybyid.GetLiabilityByIdApi

class FinanciallistDataSource(
    private val accountApi: GetAccountApi,
    private val getAccountByIdApi: GetAccountByIdApi, // 🌟 Inject API ตัวใหม่เข้ามาตรงนี้
    private val cashApi: GetCashApi,
    private val getCashByIdApi: GetCashByIdApi,
    private val investmentApi: GetInvestmentApi,
    private val insuranceApi: GetInsuranceApi,
    private val buildingApi: GetBuildingApi,
    private val getBuildingByIdApi: GetBuildingByIdApi,
    private val landApi: GetLandApi,
    private val liabilityApi: GetLiabilityApi,
    private val getInsuranceByIdApi: GetInsuranceByIdApi,
    private val getInvestmentByIdApi: GetInvestmentByIdApi, // 🚧 เดี๋ยวมาสร้าง Model ทีหลัง
    private val getLandByIdApi: GetLandByIdApi,
    private val getLiabilityByIdApi: GetLiabilityByIdApi
) {
    // --- โหลดข้อมูลทั้งหมดแบบ List (ของเดิม) ---
    suspend fun getAccount() = accountApi.getAccount()
    suspend fun getCash() = cashApi.getCash()
    suspend fun getInvestment() = investmentApi.getInvestment()
    suspend fun getInsurance() = insuranceApi.getInsurance()
    suspend fun getBuilding() = buildingApi.getBuilding()
    suspend fun getLand() = landApi.getLand()
    suspend fun getLiability() = liabilityApi.getLiability()

    // --- 🌟 โหลดข้อมูลแบบรายตัว (Get By ID) ---
    suspend fun getAccountById(id: String) = getAccountByIdApi.getAccountById(id)
    suspend fun getBuildingById(id: String) = getBuildingByIdApi.getBuildingById(id)
    suspend fun getCashById(id: String) = getCashByIdApi.getCashById(id)
    suspend fun getInsuranceById(id: String) = getInsuranceByIdApi.getInsuranceById(id)
    suspend fun getInvestmentById(id: String) = getInvestmentByIdApi.getInvestmentById(id)
    suspend fun getLandById(id: String) = getLandByIdApi.getLandById(id)
    suspend fun getLiabilityById(id: String) = getLiabilityByIdApi.getLiabilityById(id)

}