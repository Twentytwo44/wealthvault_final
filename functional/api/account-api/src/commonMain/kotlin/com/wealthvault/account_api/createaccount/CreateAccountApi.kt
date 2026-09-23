package com.wealthvault.account_api.createaccount

import com.wealthvault.domain.portfolio.BankAccountData
import com.wealthvault.domain.portfolio.BankAccountRequest

interface CreateAccountApi {
    suspend fun create(request: BankAccountRequest): BankAccountData
}
