package com.wealthvault.financiallist.data.cash

import com.wealthvault.cash_api.model.CashData
import com.wealthvault.cash_api.model.CashRequest

class CashRepositoryImpl(
    private val networkDataSource: CashNetworkDataSource,
) {
    suspend fun updateCash(id:String,request: CashRequest): Result<CashData> {
        return networkDataSource.updateCash(id,request).map { data ->
                data
        }
    }

}
