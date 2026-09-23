package com.wealthvault.data.portfolio.account.transport.deleteaccount

interface DeleteAccountApi {
    suspend fun deleteAccount(id: String)
}
