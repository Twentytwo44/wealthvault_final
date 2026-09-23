package com.wealthvault.data.portfolio.cash.transport.getcashtbyid

import com.wealthvault.domain.portfolio.CashIdData

interface GetCashByIdApi {
    suspend fun getCashById(id: String): CashIdData?
}
