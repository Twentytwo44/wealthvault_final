package com.wealthvault.cash_api.createcash

import com.wealthvault.cash_api.model.CashRequest
import com.wealthvault.cash_api.model.CashResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST

interface CreateCashApi {
    @POST("asset/cash")
    suspend fun create(@Body request: CashRequest): CashResponse
}
