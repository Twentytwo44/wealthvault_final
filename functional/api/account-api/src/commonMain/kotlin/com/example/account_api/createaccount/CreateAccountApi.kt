package com.example.account_api.createaccount

import com.example.account_api.model.BankAccountRequest
import com.example.account_api.model.BankAccountResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST

interface CreateAccountApi {
    @POST("ic_nav_asset/account")
    suspend fun create(@Body request: BankAccountRequest): BankAccountResponse
}
