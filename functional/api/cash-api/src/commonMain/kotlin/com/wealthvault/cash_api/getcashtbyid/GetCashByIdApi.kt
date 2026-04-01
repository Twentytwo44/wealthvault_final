package com.wealthvault.cash_api.getcashtbyid

import com.wealthvault.cash_api.model.CashIdResponse
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path

interface GetCashByIdApi {
    @GET("asset/cash/{id}")
    suspend fun getCashById(@Path("id") id: String): CashIdResponse
}
