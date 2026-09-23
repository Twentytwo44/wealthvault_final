package com.wealthvault.account_api.updateaccount

import com.wealthvault.domain.portfolio.BankAccountData
import com.wealthvault.domain.portfolio.BankAccountRequest

interface UpdateAccountApi {
    suspend fun updateAccount(id: String, request: BankAccountRequest): BankAccountData
}
