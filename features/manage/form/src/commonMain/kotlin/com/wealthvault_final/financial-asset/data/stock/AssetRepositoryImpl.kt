package com.wealthvault_final.`financial-asset`.data.stock

import com.wealthvault.investment_api.model.InvestmentData
import com.wealthvault.investment_api.model.InvestmentRequest

class AssetRepositoryImpl(
    private val networkDataSource: AssetNetworkDataSource,
) {
    suspend fun create(request: InvestmentRequest): Result<InvestmentData> {
        return networkDataSource.create(request).map { data ->
                data
        }
    }

}
