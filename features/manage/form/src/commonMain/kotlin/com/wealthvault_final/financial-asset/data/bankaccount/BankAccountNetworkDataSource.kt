package com.wealthvault_final.`financial-asset`.data.bankaccount

import com.wealthvault.account_api.createaccount.CreateAccountApi
import com.wealthvault.account_api.model.BankAccountData
import com.wealthvault.account_api.model.BankAccountRequest

class BankAccountNetworkDataSource(
    private val createAccountApi: CreateAccountApi,
) {
    suspend fun createBankAccount(request: BankAccountRequest): Result<BankAccountData> {
        return runCatching {
            val result = createAccountApi.create(request)
            print("result"+ result)
            result.data ?: throw IllegalArgumentException(result.error)
        }
    }
}
