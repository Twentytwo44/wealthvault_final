package com.wealthvault.financiallist.data.account

import com.wealthvault.account_api.model.BankAccountData
import com.wealthvault.account_api.model.BankAccountRequest
import com.wealthvault.account_api.updateaccount.UpdateAccountApi

class BankAccountNetworkDataSource(
    private val updateAccountApi: UpdateAccountApi,
) {
    suspend fun updateAccount(id: String,request: BankAccountRequest): Result<BankAccountData> {
        return runCatching {
            val result = updateAccountApi.updateAccount(id,request)
            print("Result Account Update:"+ result)
            result.data ?: throw IllegalArgumentException(result.error)
        }
    }
}
