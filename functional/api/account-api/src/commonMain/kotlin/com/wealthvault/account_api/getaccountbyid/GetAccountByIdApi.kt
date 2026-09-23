package com.wealthvault.account_api.getaccountbyid

import com.wealthvault.domain.portfolio.BankAccountData

interface GetAccountByIdApi {
    suspend fun getAccountById(id: String): BankAccountData?
}
