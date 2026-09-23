package com.wealthvault.data.portfolio.cash.transport.updatecash

import com.wealthvault.domain.portfolio.CashData
import com.wealthvault.domain.portfolio.CashRequest

interface UpdateCashApi {
    suspend fun updateCash(id: String, request: CashRequest): CashData
}
