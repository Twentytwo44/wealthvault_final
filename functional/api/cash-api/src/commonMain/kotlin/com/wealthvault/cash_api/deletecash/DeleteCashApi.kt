package com.wealthvault.cash_api.deletecash

interface DeleteCashApi {
    suspend fun deleteCash(id: String)
}
