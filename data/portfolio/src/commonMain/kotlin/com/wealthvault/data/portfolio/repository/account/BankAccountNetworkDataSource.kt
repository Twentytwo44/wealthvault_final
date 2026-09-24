package com.wealthvault.data.portfolio.repository.account

import com.wealthvault.domain.portfolio.BankAccountRequest
import com.wealthvault.data.portfolio.account.transport.updateaccount.UpdateAccountApi
import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.architecture.runSuspendAppCatching
import com.wealthvault.domain.portfolio.BankAccountData

class BankAccountNetworkDataSource(
    private val updateAccountApi: UpdateAccountApi,
) {
    suspend fun updateAccount(id: String, request: BankAccountRequest): AppResult<BankAccountData> {
        return runSuspendAppCatching {
            updateAccountApi.updateAccount(id, request)
        }
    }
}
