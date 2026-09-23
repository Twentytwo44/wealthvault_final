package com.wealthvault.cash_api.getcash

import com.wealthvault.domain.portfolio.GetCashData

interface GetCashApi {
    suspend fun getCash(): List<GetCashData>
}
