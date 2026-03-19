package com.wealthvault.investment_api.updateinvestment

import com.wealthvault.investment_api.model.InvestmentRequest
import com.wealthvault.investment_api.model.InvestmentResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.PATCH
import de.jensklingenberg.ktorfit.http.Path

interface UpdateInvestmentApi {
    @PATCH("ic_nav_asset/invest/{id}")
    suspend fun updateInvestment(@Path("id") id: String, @Body request: InvestmentRequest): InvestmentResponse
}
