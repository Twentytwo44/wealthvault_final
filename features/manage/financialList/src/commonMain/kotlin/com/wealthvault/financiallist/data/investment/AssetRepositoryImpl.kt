package com.wealthvault.financiallist.data.investment

import com.wealthvault.investment_api.model.InvestmentData
import com.wealthvault.investment_api.model.InvestmentRequest

class AssetRepositoryImpl(
    private val networkDataSource: AssetNetworkDataSource,
) {
    suspend fun updateInvestment(id:String,request: InvestmentRequest): Result<InvestmentData> {
        return networkDataSource.updateInvestment(id,request).map { data ->
                data
        }
    }

}
