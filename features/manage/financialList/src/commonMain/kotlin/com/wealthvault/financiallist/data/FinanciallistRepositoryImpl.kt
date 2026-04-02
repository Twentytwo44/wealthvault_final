package com.wealthvault.financiallist.data

import com.wealthvault.account_api.model.AccountData
import com.wealthvault.account_api.model.BankAccountData
import com.wealthvault.cash_api.model.GetCashData
import com.wealthvault.investment_api.model.GetInvestmentData
import com.wealthvault.insurance_api.model.GetInsuranceData
import com.wealthvault.building_api.model.GetBuildingData
import com.wealthvault.land_api.model.GetLandData
import com.wealthvault.liability_api.model.GetLiabilityData
import com.wealthvault.building_api.model.BuildingIdData
import com.wealthvault.cash_api.model.CashIdData
import com.wealthvault.insurance_api.model.InsuranceIdData
import com.wealthvault.investment_api.model.InvestmentIdData
import com.wealthvault.land_api.model.LandIdData
import com.wealthvault.liability_api.model.LiabilityIdData

class FinanciallistRepositoryImpl(
    private val dataSource: FinanciallistDataSource
) {
    // --- ดึงข้อมูลแบบรายตัว (Get By ID) ---
    suspend fun getAccountById(id: String): Result<BankAccountData> = runCatching {
        val response = dataSource.getAccountById(id)
        response.data ?: throw Exception("ไม่พบข้อมูลบัญชีเงินฝาก")
    }
    suspend fun getBuildingById(id: String): Result<BuildingIdData> = runCatching {
        val response = dataSource.getBuildingById(id)
        response.data ?: throw Exception("ไม่พบข้อมูลอาคาร/สิ่งปลูกสร้าง")
    }
    suspend fun getCashById(id: String): Result<CashIdData> = runCatching {
        val response = dataSource.getCashById(id)
        response.data ?: throw Exception("ไม่พบข้อมูลเงินสด/ทองคำ")
    }
    suspend fun getInsuranceById(id: String): Result<InsuranceIdData> = runCatching {
        dataSource.getInsuranceById(id).data ?: throw Exception("ไม่พบข้อมูลประกัน")
    }
    suspend fun getInvestmentById(id: String): Result<InvestmentIdData> = runCatching {
        dataSource.getInvestmentById(id).data ?: throw Exception("ไม่พบข้อมูลการลงทุน")
    }
    suspend fun getLandById(id: String): Result<LandIdData> = runCatching {
        dataSource.getLandById(id).data ?: throw Exception("ไม่พบข้อมูลที่ดิน")
    }
    suspend fun getLiabilityById(id: String): Result<LiabilityIdData> = runCatching {
        dataSource.getLiabilityById(id).data ?: throw Exception("ไม่พบข้อมูลหนี้สิน/รายจ่าย")
    }

    // --- ดึงข้อมูลทั้งหมดแบบ List ---
    suspend fun getAccounts(): Result<List<AccountData>> = runCatching {
        dataSource.getAccount().data ?: emptyList()
    }
    suspend fun getCashes(): Result<List<GetCashData>> = runCatching {
        dataSource.getCash().data ?: emptyList()
    }
    suspend fun getInvestments(): Result<List<GetInvestmentData>> = runCatching {
        dataSource.getInvestment().data ?: emptyList()
    }
    suspend fun getInsurances(): Result<List<GetInsuranceData>> = runCatching {
        dataSource.getInsurance().data ?: emptyList()
    }
    suspend fun getBuildings(): Result<List<GetBuildingData>> = runCatching {
        dataSource.getBuilding().data ?: emptyList()
    }
    suspend fun getLands(): Result<List<GetLandData>> = runCatching {
        dataSource.getLand().data ?: emptyList()
    }
    suspend fun getLiabilities(): Result<List<GetLiabilityData>> = runCatching {
        dataSource.getLiability().data ?: emptyList()
    }

    // 🌟 --- เพิ่มฟังก์ชันลบข้อมูล (Delete) ตรงนี้ครับ ---
    suspend fun deleteAsset(id: String, type: String): Result<Boolean> = runCatching {
        val response = dataSource.deleteAsset(id, type)
        // เช็คว่า success เป็น true หรือเปล่า
        if (response.data?.success == true) {
            true
        } else {
            throw Exception(response.status ?: "เกิดข้อผิดพลาดในการลบข้อมูล")
        }
    }
}