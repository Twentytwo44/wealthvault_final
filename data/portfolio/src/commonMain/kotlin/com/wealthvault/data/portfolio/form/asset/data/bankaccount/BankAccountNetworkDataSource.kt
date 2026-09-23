package com.wealthvault.data.portfolio.form.asset.data.bankaccount

import com.wealthvault.data.portfolio.account.transport.createaccount.CreateAccountApi
import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.architecture.runSuspendAppCatching
import com.wealthvault.domain.portfolio.BankAccountData
import com.wealthvault.domain.portfolio.BankAccountRequest

class BankAccountNetworkDataSource(
    private val createAccountApi: CreateAccountApi,
) {
    suspend fun createBankAccount(request: BankAccountRequest): AppResult<BankAccountData> {
        return runSuspendAppCatching {
            createAccountApi.create(request)
        }
    }
}
