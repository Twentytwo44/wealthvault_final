package com.wealthvault_final.`financial-asset`.data.stock

import com.wealthvault.investment_api.createinvestment.CreateInvestmentApi
import com.wealthvault.investment_api.model.InvestmentData
import com.wealthvault.investment_api.model.InvestmentRequest

class AssetNetworkDataSource(
    private val createInvestmentApi: CreateInvestmentApi,
) {
    suspend fun create(request: InvestmentRequest): Result<InvestmentData> {
        return runCatching {
            val result = createInvestmentApi.create(request)
            print("result"+ result)
            result.data ?: throw IllegalArgumentException(result.error)
        }
    }
}
