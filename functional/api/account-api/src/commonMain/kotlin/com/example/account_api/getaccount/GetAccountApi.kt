package com.example.account_api.getaccount

import com.example.account_api.model.BankAccountResponse
import de.jensklingenberg.ktorfit.http.GET

interface GetAccountApi {
    @GET("asset/account")
    suspend fun getAccount(): BankAccountResponse
}
