package com.wealthvault.investment_api.getinvestment

import com.wealthvault.investment_api.model.GetInvestmentResponse
import de.jensklingenberg.ktorfit.http.GET

interface GetInvestmentApi {
    @GET("asset/invest")
    suspend fun getInvestment(): GetInvestmentResponse
}
