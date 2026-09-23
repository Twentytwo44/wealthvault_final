package com.wealthvault.data.portfolio.account.transport.getaccountbyid

import com.wealthvault.domain.portfolio.BankAccountData

interface GetAccountByIdApi {
    suspend fun getAccountById(id: String): BankAccountData?
}
