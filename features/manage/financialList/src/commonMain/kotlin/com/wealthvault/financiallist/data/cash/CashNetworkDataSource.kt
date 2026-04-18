package com.wealthvault.financiallist.data.cash

import com.wealthvault.cash_api.model.CashData
import com.wealthvault.cash_api.model.CashRequest
import com.wealthvault.cash_api.updatecash.UpdateCashApi

class CashNetworkDataSource(
    private val updateCashApi: UpdateCashApi,
) {
    suspend fun updateCash(id:String,request: CashRequest): Result<CashData> {
        return runCatching {
            val result = updateCashApi.updateCash(id,request)
            print("Result Cash Update:"+ result)
            result.data ?: throw IllegalArgumentException(result.error)
        }
    }
}
