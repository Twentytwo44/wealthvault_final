package com.wealthvault.data.portfolio.form.asset.data.cash

import com.wealthvault.data.portfolio.cash.transport.createcash.CreateCashApi
import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.architecture.runSuspendAppCatching
import com.wealthvault.domain.portfolio.CashData
import com.wealthvault.domain.portfolio.CashRequest

class CashNetworkDataSource(
    private val createCashApi: CreateCashApi,
) {
    suspend fun create(request: CashRequest): AppResult<CashData> {
        return runSuspendAppCatching {
            val result = createCashApi.create(request)
            result
        }
    }
}
