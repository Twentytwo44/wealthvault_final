package com.wealthvault.account_api.deleteaccount

interface DeleteAccountApi {
    suspend fun deleteAccount(id: String)
}
