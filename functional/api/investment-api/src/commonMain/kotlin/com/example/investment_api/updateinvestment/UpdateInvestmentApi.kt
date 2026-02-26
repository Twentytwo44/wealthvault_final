package com.example.investment_api.updateinvestment

import com.example.investment_api.model.InvestmentRequest
import com.example.investment_api.model.InvestmentResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.PATCH
import de.jensklingenberg.ktorfit.http.Path

interface UpdateInvestmentApi {
    @PATCH("asset/invest/{id}")
    suspend fun updateInvestment(@Path("id") id: String, @Body request: InvestmentRequest): InvestmentResponse
}
