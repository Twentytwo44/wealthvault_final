package com.wealthvault.account_api.getaccount

import com.wealthvault.account_api.model.AccountResponse
import de.jensklingenberg.ktorfit.http.GET

interface GetAccountApi {
    @GET("asset/account/")
    suspend fun getAccount(): AccountResponse
}
