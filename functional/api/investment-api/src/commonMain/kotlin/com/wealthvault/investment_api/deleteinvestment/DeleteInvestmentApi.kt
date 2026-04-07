package com.wealthvault.investment_api.deleteinvestment

import com.wealthvault.core.model.DeleteBaseResponse
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.Path

interface DeleteInvestmentApi {
    @DELETE("asset/invest/{id}/")
    suspend fun deleteInvestment(@Path("id") id: String): DeleteInvestmentResponse
}
