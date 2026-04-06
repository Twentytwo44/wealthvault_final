package com.wealthvault_final.`financial-asset`.data.cash

import com.wealthvault.cash_api.createcash.CreateCashApi
import com.wealthvault.cash_api.model.CashData
import com.wealthvault.cash_api.model.CashRequest

class CashNetworkDataSource(
    private val createCashApi: CreateCashApi,
) {
    suspend fun create(request: CashRequest): Result<CashData> {
        return runCatching {
            val result = createCashApi.create(request)
            print("result"+ result)
            result.data ?: throw IllegalArgumentException(result.error)
        }
    }
}
