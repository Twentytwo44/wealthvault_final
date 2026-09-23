package com.wealthvault.investment_api.updateinvestment

import com.wealthvault.domain.portfolio.InvestmentData
import com.wealthvault.domain.portfolio.InvestmentRequest

interface UpdateInvestmentApi {
    suspend fun updateInvestment(id: String, request: InvestmentRequest): InvestmentData
}
