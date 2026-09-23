package com.wealthvault.data.portfolio.cash.transport.getcash

import com.wealthvault.domain.portfolio.GetCashData

interface GetCashApi {
    suspend fun getCash(): List<GetCashData>
}
