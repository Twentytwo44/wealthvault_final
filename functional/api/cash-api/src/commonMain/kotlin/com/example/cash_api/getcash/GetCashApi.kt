package com.example.cash_api.getcash

import com.example.cash_api.model.GetCashResponse
import de.jensklingenberg.ktorfit.http.GET

interface GetCashApi {
    @GET("ic_nav_asset/cash")
    suspend fun getCash(): GetCashResponse
}
