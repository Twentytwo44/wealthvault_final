package com.wealthvault_final.`financial-asset`.data.bankaccount

import com.wealthvault.account_api.model.BankAccountData
import com.wealthvault.account_api.model.BankAccountRequest

class BankAccountRepositoryImpl(
    private val networkDataSource: BankAccountNetworkDataSource,
) {
    suspend fun createBankAccount(request: BankAccountRequest): Result<BankAccountData> {
        return networkDataSource.createBankAccount(request).map { data ->
                data
        }
    }

}
