package com.example.investment_api.getinvestmentbyid

import com.example.investment_api.model.InvestmentIdResponse
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path

interface GetInvestmentByIdApi {
    @GET("asset/invest/{id}")
    suspend fun getInvestmentById(@Path("id") id: String): InvestmentIdResponse
}
