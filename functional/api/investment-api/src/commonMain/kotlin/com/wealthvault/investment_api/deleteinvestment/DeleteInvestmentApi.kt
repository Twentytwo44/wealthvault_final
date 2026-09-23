package com.wealthvault.investment_api.deleteinvestment

interface DeleteInvestmentApi {
    suspend fun deleteInvestment(id: String)
}
