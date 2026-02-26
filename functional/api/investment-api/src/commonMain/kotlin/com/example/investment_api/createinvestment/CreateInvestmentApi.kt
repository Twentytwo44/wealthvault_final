package com.example.investment_api.createcash

import com.example.investment_api.model.InvestmentRequest
import com.example.investment_api.model.InvestmentResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST

interface CreateInvestmentApi {
    @POST("asset/invest")
    suspend fun create(@Body request: InvestmentRequest): InvestmentResponse
}
