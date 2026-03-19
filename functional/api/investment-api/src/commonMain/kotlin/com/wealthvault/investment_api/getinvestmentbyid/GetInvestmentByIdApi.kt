package com.wealthvault.investment_api.getinvestmentbyid

import com.wealthvault.investment_api.model.InvestmentIdResponse
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path

interface GetInvestmentByIdApi {
    @GET("ic_nav_asset/invest/{id}")
    suspend fun getInvestmentById(@Path("id") id: String): InvestmentIdResponse
}
