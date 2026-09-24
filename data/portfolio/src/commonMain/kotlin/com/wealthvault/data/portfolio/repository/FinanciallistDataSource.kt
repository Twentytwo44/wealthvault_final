package com.wealthvault.data.portfolio.repository

import com.wealthvault.data.portfolio.account.transport.getaccount.GetAccountApi
import com.wealthvault.data.portfolio.account.transport.getaccountbyid.GetAccountByIdApi
import com.wealthvault.data.portfolio.cash.transport.getcash.GetCashApi
import com.wealthvault.data.portfolio.investment.transport.getinvestment.GetInvestmentApi
import com.wealthvault.data.portfolio.insurance.transport.getinsurance.GetInsuranceApi
import com.wealthvault.data.portfolio.building.transport.getbuilding.GetBuildingApi
import com.wealthvault.data.portfolio.building.transport.getbuildingbyid.GetBuildingByIdApi
import com.wealthvault.data.portfolio.cash.transport.getcashtbyid.GetCashByIdApi
import com.wealthvault.data.portfolio.insurance.transport.getinsurancetbyid.GetInsuranceByIdApi
import com.wealthvault.data.portfolio.investment.transport.getinvestmentbyid.GetInvestmentByIdApi
import com.wealthvault.data.portfolio.land.transport.getland.GetLandApi
import com.wealthvault.data.portfolio.land.transport.getlandbyid.GetLandByIdApi
import com.wealthvault.data.portfolio.liability.transport.getliability.GetLiabilityApi
import com.wealthvault.data.portfolio.liability.transport.getliabilitybyid.GetLiabilityByIdApi

// 🌟 Import ตัว Delete API เข้ามาด้วย (แก้ Package ให้ตรงกับของจริงนะครับ)
import com.wealthvault.data.portfolio.account.transport.deleteaccount.DeleteAccountApi
import com.wealthvault.data.portfolio.cash.transport.deletecash.DeleteCashApi
import com.wealthvault.data.portfolio.investment.transport.deleteinvestment.DeleteInvestmentApi
import com.wealthvault.data.portfolio.insurance.transport.deleteinsurance.DeleteInsuranceApi
import com.wealthvault.data.portfolio.building.transport.deletebuilding.DeleteBuildingApi
import com.wealthvault.data.portfolio.land.transport.deleteland.DeleteLandApi
import com.wealthvault.data.portfolio.liability.transport.deleteliability.DeleteLiabilityApi

class FinanciallistDataSource(
    // --- Get All APIs ---
    private val accountApi: GetAccountApi,
    private val cashApi: GetCashApi,
    private val investmentApi: GetInvestmentApi,
    private val insuranceApi: GetInsuranceApi,
    private val buildingApi: GetBuildingApi,
    private val landApi: GetLandApi,
    private val liabilityApi: GetLiabilityApi,

    // --- Get By ID APIs ---
    private val getAccountByIdApi: GetAccountByIdApi,
    private val getCashByIdApi: GetCashByIdApi,
    private val getBuildingByIdApi: GetBuildingByIdApi,
    private val getInsuranceByIdApi: GetInsuranceByIdApi,
    private val getInvestmentByIdApi: GetInvestmentByIdApi,
    private val getLandByIdApi: GetLandByIdApi,
    private val getLiabilityByIdApi: GetLiabilityByIdApi,

    // 🌟 --- Delete APIs (เพิ่มเข้ามาเพื่อใช้ลบ) ---
    private val deleteAccountApi: DeleteAccountApi,
    private val deleteCashApi: DeleteCashApi,
    private val deleteInvestmentApi: DeleteInvestmentApi,
    private val deleteInsuranceApi: DeleteInsuranceApi,
    private val deleteBuildingApi: DeleteBuildingApi,
    private val deleteLandApi: DeleteLandApi,
    private val deleteLiabilityApi: DeleteLiabilityApi
) : FinanciallistRemoteDataSource {
    // --- โหลดข้อมูลทั้งหมดแบบ List ---
    override suspend fun getAccount() = accountApi.getAccount()
    override suspend fun getCash() = cashApi.getCash()
    override suspend fun getInvestment() = investmentApi.getInvestment()
    override suspend fun getInsurance() = insuranceApi.getInsurance()
    override suspend fun getBuilding() = buildingApi.getBuilding()
    override suspend fun getLand() = landApi.getLand()
    override suspend fun getLiability() = liabilityApi.getLiability()

    // --- โหลดข้อมูลแบบรายตัว (Get By ID) ---
    override suspend fun getAccountById(id: String) = getAccountByIdApi.getAccountById(id)
    override suspend fun getBuildingById(id: String) = getBuildingByIdApi.getBuildingById(id)
    override suspend fun getCashById(id: String) = getCashByIdApi.getCashById(id)
    override suspend fun getInsuranceById(id: String) = getInsuranceByIdApi.getInsuranceById(id)
    override suspend fun getInvestmentById(id: String) = getInvestmentByIdApi.getInvestmentById(id)
    override suspend fun getLandById(id: String) = getLandByIdApi.getLandById(id)
    override suspend fun getLiabilityById(id: String) = getLiabilityByIdApi.getLiabilityById(id)

    // 🌟 --- ลบข้อมูล (Delete) ใส่ปีกกาและ when ให้เรียบร้อย ---
    override suspend fun deleteAsset(id: String, type: String): Boolean {

        // 🌟 1. เอาคำว่า return ออกไปเลย ปล่อยให้มันยิง API เฉยๆ
        when (type) {
            "account" -> deleteAccountApi.deleteAccount(id)
            "cash" -> deleteCashApi.deleteCash(id)
            "investment" -> deleteInvestmentApi.deleteInvestment(id)
            "insurance" -> deleteInsuranceApi.deleteInsurance(id)
            "building" -> deleteBuildingApi.deleteBuilding(id)
            "land" -> deleteLandApi.deleteLand(id)
            "liability", "expense" -> deleteLiabilityApi.deleteLiability(id)
            else -> throw IllegalArgumentException("ไม่รู้จักประเภททรัพย์สิน: $type")
        }

        // 🌟 2. ถ้ายิงผ่าน ไม่เด้งเข้า else เราค่อยมา return ค่าจำลองส่งกลับไปทีเดียวตอนจบครับ
        return true
    }
}
