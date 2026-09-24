package com.wealthvault.data.portfolio.repository.investment

import com.wealthvault.domain.portfolio.InvestmentRequest
import com.wealthvault.domain.portfolio.InvestmentData
import com.wealthvault.data.portfolio.investment.transport.updateinvestment.UpdateInvestmentApi
import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.architecture.runSuspendAppCatching

class AssetNetworkDataSource(
    private val updateInvestmentApi: UpdateInvestmentApi,
) {
    suspend fun updateInvestment(id:String,request: InvestmentRequest): AppResult<InvestmentData> {
        return runSuspendAppCatching {
            updateInvestmentApi.updateInvestment(id, request)
        }
    }
}
