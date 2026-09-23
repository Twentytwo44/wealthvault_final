package com.wealthvault.data.portfolio.investment.transport.deleteinvestment

interface DeleteInvestmentApi {
    suspend fun deleteInvestment(id: String)
}
