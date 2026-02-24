package com.example.account_api.updateaccount

import com.example.account_api.model.BankAccountRequest
import com.example.account_api.model.BankAccountResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.PATCH
import de.jensklingenberg.ktorfit.http.Path

interface UpdateAccountApi {
    @PATCH("asset/account/{id}")
    suspend fun updateAccount(@Path("id") id: String, @Body request: BankAccountRequest): BankAccountResponse
}
