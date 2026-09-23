package com.wealthvault.investment_api.getinvestment

import com.wealthvault.domain.portfolio.GetInvestmentData

interface GetInvestmentApi {
    suspend fun getInvestment(): List<GetInvestmentData>
}
