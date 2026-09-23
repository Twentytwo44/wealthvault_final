package com.wealthvault.cash_api.getcashtbyid

import com.wealthvault.domain.portfolio.CashIdData

interface GetCashByIdApi {
    suspend fun getCashById(id: String): CashIdData?
}
