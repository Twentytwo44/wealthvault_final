package com.wealthvault.data.portfolio.investment.transport.updateinvestment

import com.wealthvault.domain.portfolio.InvestmentData
import com.wealthvault.domain.portfolio.InvestmentRequest

interface UpdateInvestmentApi {
    suspend fun updateInvestment(id: String, request: InvestmentRequest): InvestmentData
}
