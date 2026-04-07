package com.wealthvault.account_api.getaccount

import com.wealthvault.account_api.model.AccountResponse // 🌟 นำเข้าตัวใหม่
import de.jensklingenberg.ktorfit.http.GET

interface GetAccountApi {
    @GET("asset/account/")
    suspend fun getAccount(): BankAccountResponse
}
