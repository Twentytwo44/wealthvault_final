package com.example.account_api.getaccountbyid

import com.example.account_api.model.BankAccountResponse
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path

interface GetAccountByIdApi {
    @GET("asset/account/{id}")
    suspend fun getAccountById(@Path("id") id: String): BankAccountResponse
}
