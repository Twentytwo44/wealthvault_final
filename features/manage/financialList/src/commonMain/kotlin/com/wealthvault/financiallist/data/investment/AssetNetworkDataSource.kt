package com.wealthvault.financiallist.data.investment

import com.wealthvault.investment_api.model.InvestmentData
import com.wealthvault.investment_api.model.InvestmentRequest
import com.wealthvault.investment_api.updateinvestment.UpdateInvestmentApi

class AssetNetworkDataSource(
    private val updateInvestmentApi: UpdateInvestmentApi,
) {
    suspend fun updateInvestment(id:String,request: InvestmentRequest): Result<InvestmentData> {
        return runCatching {
            val result = updateInvestmentApi.updateInvestment(id,request)
            print("Result Investment Update:"+ result)
            result.data ?: throw IllegalArgumentException(result.error)
        }
    }
}
