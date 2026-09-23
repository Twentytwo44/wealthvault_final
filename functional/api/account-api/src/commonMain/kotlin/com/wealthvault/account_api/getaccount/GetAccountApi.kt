package com.wealthvault.account_api.getaccount

import com.wealthvault.domain.portfolio.AccountData

interface GetAccountApi {
    suspend fun getAccount(): List<AccountData>
}
