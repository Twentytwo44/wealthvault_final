package com.wealthvault.data.portfolio.account.transport.getaccount

import com.wealthvault.domain.portfolio.AccountData

interface GetAccountApi {
    suspend fun getAccount(): List<AccountData>
}
