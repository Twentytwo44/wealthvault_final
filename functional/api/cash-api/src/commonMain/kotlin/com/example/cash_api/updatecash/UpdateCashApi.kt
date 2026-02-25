package com.example.cash_api.updatecash

import com.example.cash_api.model.CashRequest
import com.example.cash_api.model.CashResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.PATCH
import de.jensklingenberg.ktorfit.http.Path

interface UpdateCashApi {
    @PATCH("asset/cash/{id}")
    suspend fun updateCash(@Path("id") id: String, @Body request: CashRequest): CashResponse
}
