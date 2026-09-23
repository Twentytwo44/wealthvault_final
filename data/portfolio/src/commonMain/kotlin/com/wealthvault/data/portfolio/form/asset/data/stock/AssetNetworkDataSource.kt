package com.wealthvault.data.portfolio.form.asset.data.stock

import com.wealthvault.data.portfolio.investment.transport.createinvestment.CreateInvestmentApi
import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.architecture.runSuspendAppCatching
import com.wealthvault.domain.portfolio.InvestmentData
import com.wealthvault.domain.portfolio.InvestmentRequest

class AssetNetworkDataSource(
    private val createInvestmentApi: CreateInvestmentApi,
) {
    suspend fun create(request: InvestmentRequest): AppResult<InvestmentData> {
        return runSuspendAppCatching {
            val result = createInvestmentApi.create(request)
            result
        }
    }
}
