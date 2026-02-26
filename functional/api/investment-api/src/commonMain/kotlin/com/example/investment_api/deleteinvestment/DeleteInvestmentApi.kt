package com.example.investment_api.deleteinvestment

import com.example.investment_api.model.DeleteInvestmentResponse
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.Path

interface DeleteInvestmentApi {
    @DELETE("asset/invest/{id}")
    suspend fun deleteInvestmen(@Path("id") id: String): DeleteInvestmentResponse
}
