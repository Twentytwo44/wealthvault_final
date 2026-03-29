package com.wealthvault.financiallist.data

import com.wealthvault.account_api.model.AccountData
import com.wealthvault.cash_api.model.GetCashData
import com.wealthvault.investment_api.model.GetInvestmentData
import com.wealthvault.insurance_api.model.GetInsuranceData
import com.wealthvault.building_api.model.GetBuildingData
import com.wealthvault.land_api.model.GetLandData
import com.wealthvault.liability_api.model.GetLiabilityData

class FinanciallistRepositoryImpl(
    private val dataSource: FinanciallistDataSource
) {
    // 🌟 สังเกตตรง emptyList<...>() นะครับ ต้องใส่ให้ครบทุกบรรทัด
    suspend fun getAccounts(): Result<List<AccountData>> = runCatching {
        dataSource.getAccount().data ?: emptyList<AccountData>()
    }

    suspend fun getCashes(): Result<List<GetCashData>> = runCatching {
        dataSource.getCash().data ?: emptyList<GetCashData>()
    }

    suspend fun getInvestments(): Result<List<GetInvestmentData>> = runCatching {
        dataSource.getInvestment().data ?: emptyList<GetInvestmentData>()
    }

    suspend fun getInsurances(): Result<List<GetInsuranceData>> = runCatching {
        dataSource.getInsurance().data ?: emptyList<GetInsuranceData>()
    }

    suspend fun getBuildings(): Result<List<GetBuildingData>> = runCatching {
        dataSource.getBuilding().data ?: emptyList<GetBuildingData>()
    }

    suspend fun getLands(): Result<List<GetLandData>> = runCatching {
        dataSource.getLand().data ?: emptyList<GetLandData>()
    }

    suspend fun getLiabilities(): Result<List<GetLiabilityData>> = runCatching {
        dataSource.getLiability().data ?: emptyList<GetLiabilityData>()
    }
}