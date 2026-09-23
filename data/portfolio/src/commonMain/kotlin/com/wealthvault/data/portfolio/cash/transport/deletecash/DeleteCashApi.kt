package com.wealthvault.data.portfolio.cash.transport.deletecash

interface DeleteCashApi {
    suspend fun deleteCash(id: String)
}
