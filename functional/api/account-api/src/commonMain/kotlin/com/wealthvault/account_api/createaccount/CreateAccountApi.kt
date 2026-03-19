package com.wealthvault.account_api.createaccount

import com.wealthvault.account_api.model.BankAccountRequest
import com.wealthvault.account_api.model.BankAccountResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST

interface CreateAccountApi {
    @POST("ic_nav_asset/account")
    suspend fun create(@Body request: BankAccountRequest): BankAccountResponse
}
