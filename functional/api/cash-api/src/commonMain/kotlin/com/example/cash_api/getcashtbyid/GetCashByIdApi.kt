package com.example.cash_api.getcashtbyid

import com.example.cash_api.model.CashIdResponse
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path

interface GetCashByIdApi {
    @GET("asset/cash/{id}")
    suspend fun getCashById(@Path("id") id: String): CashIdResponse
}
