package com.wealthvault.financiallist.data.account

import com.wealthvault.account_api.model.BankAccountData
import com.wealthvault.account_api.model.BankAccountRequest

class BankAccountRepositoryImpl(
    private val networkDataSource: BankAccountNetworkDataSource,
) {
    suspend fun updateAccount(id: String,request: BankAccountRequest): Result<BankAccountData> {
        return networkDataSource.updateAccount(id,request).map { data ->
                data
        }
    }

}
