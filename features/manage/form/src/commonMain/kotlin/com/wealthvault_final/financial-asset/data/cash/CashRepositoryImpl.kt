package com.wealthvault_final.`financial-asset`.data.cash

import com.wealthvault.cash_api.model.CashData
import com.wealthvault.cash_api.model.CashRequest

class CashRepositoryImpl(
    private val networkDataSource: CashNetworkDataSource,
) {
    suspend fun create(request: CashRequest): Result<CashData> {
        return networkDataSource.create(request).map { data ->
                data
        }
    }

}
