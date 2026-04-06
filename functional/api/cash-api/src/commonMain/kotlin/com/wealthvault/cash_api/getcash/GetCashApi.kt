package com.wealthvault.cash_api.getcash

import com.wealthvault.cash_api.model.GetCashResponse
import de.jensklingenberg.ktorfit.http.GET

interface GetCashApi {
    @GET("asset/cash")
    suspend fun getCash(): GetCashResponse
}
