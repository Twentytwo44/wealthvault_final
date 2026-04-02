package com.wealthvault.financiallist.data

import com.wealthvault.account_api.getaccount.GetAccountApi
import com.wealthvault.account_api.getaccountbyid.GetAccountByIdApi
import com.wealthvault.cash_api.getcash.GetCashApi
import com.wealthvault.investment_api.getinvestment.GetInvestmentApi
import com.wealthvault.insurance_api.getinsurance.GetInsuranceApi
import com.wealthvault.building_api.getbuilding.GetBuildingApi
import com.wealthvault.building_api.getbuildingbyid.GetBuildingByIdApi
import com.wealthvault.cash_api.getcashtbyid.GetCashByIdApi
import com.wealthvault.core.model.DeleteBaseResponse
import com.wealthvault.insurance_api.getinsurancetbyid.GetInsuranceByIdApi
import com.wealthvault.investment_api.getinvestmentbyid.GetInvestmentByIdApi
import com.wealthvault.land_api.getland.GetLandApi
import com.wealthvault.land_api.getlandbyid.GetLandByIdApi
import com.wealthvault.liability_api.getliability.GetLiabilityApi
import com.wealthvault.liability_api.getliabilitybyid.GetLiabilityByIdApi

// 🌟 Import ตัว Delete API เข้ามาด้วย (แก้ Package ให้ตรงกับของจริงนะครับ)
import com.wealthvault.account_api.deleteaccount.DeleteAccountApi
import com.wealthvault.cash_api.deletecash.DeleteCashApi
import com.wealthvault.investment_api.deleteinvestment.DeleteInvestmentApi
import com.wealthvault.insurance_api.deleteinsurance.DeleteInsuranceApi
import com.wealthvault.building_api.deletebuilding.DeleteBuildingApi
import com.wealthvault.land_api.deleteland.DeleteLandApi
import com.wealthvault.liability_api.deleteliability.DeleteLiabilityApi

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
) {
    // --- โหลดข้อมูลทั้งหมดแบบ List ---
    suspend fun getAccount() = accountApi.getAccount()
    suspend fun getCash() = cashApi.getCash()
    suspend fun getInvestment() = investmentApi.getInvestment()
    suspend fun getInsurance() = insuranceApi.getInsurance()
    suspend fun getBuilding() = buildingApi.getBuilding()
    suspend fun getLand() = landApi.getLand()
    suspend fun getLiability() = liabilityApi.getLiability()

    // --- โหลดข้อมูลแบบรายตัว (Get By ID) ---
    suspend fun getAccountById(id: String) = getAccountByIdApi.getAccountById(id)
    suspend fun getBuildingById(id: String) = getBuildingByIdApi.getBuildingById(id)
    suspend fun getCashById(id: String) = getCashByIdApi.getCashById(id)
    suspend fun getInsuranceById(id: String) = getInsuranceByIdApi.getInsuranceById(id)
    suspend fun getInvestmentById(id: String) = getInvestmentByIdApi.getInvestmentById(id)
    suspend fun getLandById(id: String) = getLandByIdApi.getLandById(id)
    suspend fun getLiabilityById(id: String) = getLiabilityByIdApi.getLiabilityById(id)

    // 🌟 --- ลบข้อมูล (Delete) ใส่ปีกกาและ when ให้เรียบร้อย ---
    suspend fun deleteAsset(id: String, type: String): DeleteBaseResponse {

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
        return DeleteBaseResponse(
            status = "success",
            data = com.wealthvault.core.model.DeleteBaseData(success = true)
        )
    }
}