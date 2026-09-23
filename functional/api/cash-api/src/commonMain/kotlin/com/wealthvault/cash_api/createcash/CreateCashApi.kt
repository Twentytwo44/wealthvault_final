package com.wealthvault.cash_api.createcash

import com.wealthvault.domain.portfolio.CashData
import com.wealthvault.domain.portfolio.CashRequest

interface CreateCashApi {
    suspend fun create(request: CashRequest): CashData
}
