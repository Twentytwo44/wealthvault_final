package com.wealthvault.data.portfolio.repository.cash

import com.wealthvault.domain.portfolio.CashRequest
import com.wealthvault.data.portfolio.cash.transport.updatecash.UpdateCashApi
import com.wealthvault.domain.portfolio.CashData
import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.architecture.runSuspendAppCatching

class CashNetworkDataSource(
    private val updateCashApi: UpdateCashApi,
) {
    suspend fun updateCash(id: String, request: CashRequest): AppResult<CashData> {
        return runSuspendAppCatching {
            updateCashApi.updateCash(id, request)
        }
    }
}
