package com.wealthvault.investment_api.getinvestmentbyid

import com.wealthvault.domain.portfolio.InvestmentIdData

interface GetInvestmentByIdApi {
    suspend fun getInvestmentById(id: String): InvestmentIdData?
}
