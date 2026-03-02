package com.example.investment_api.getinvestment

import com.example.investment_api.model.GetInvestmentResponse
import de.jensklingenberg.ktorfit.http.GET

interface GetInvestmentApi {
    @GET("asset/invest")
    suspend fun getInvestment(): GetInvestmentResponse
}
