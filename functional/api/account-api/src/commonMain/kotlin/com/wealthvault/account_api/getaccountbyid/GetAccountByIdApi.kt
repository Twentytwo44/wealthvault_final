package com.wealthvault.account_api.getaccountbyid

import com.wealthvault.account_api.model.BankAccountResponse
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path

interface GetAccountByIdApi {
    @GET("asset/account/{id}")
    suspend fun getAccountById(@Path("id") id: String): BankAccountResponse
}
