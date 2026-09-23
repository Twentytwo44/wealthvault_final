package com.wealthvault.investment_api.createinvestment

import com.wealthvault.domain.portfolio.InvestmentData
import com.wealthvault.domain.portfolio.InvestmentRequest

interface CreateInvestmentApi {
    suspend fun create(request: InvestmentRequest): InvestmentData
}
