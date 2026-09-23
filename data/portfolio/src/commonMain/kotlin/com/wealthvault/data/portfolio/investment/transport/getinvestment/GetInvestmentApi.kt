package com.wealthvault.data.portfolio.investment.transport.getinvestment

import com.wealthvault.domain.portfolio.GetInvestmentData

interface GetInvestmentApi {
    suspend fun getInvestment(): List<GetInvestmentData>
}
