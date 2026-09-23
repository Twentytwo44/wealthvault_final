package com.wealthvault.data.portfolio.investment.transport.getinvestmentbyid

import com.wealthvault.domain.portfolio.InvestmentIdData

interface GetInvestmentByIdApi {
    suspend fun getInvestmentById(id: String): InvestmentIdData?
}
