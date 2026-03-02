package com.example.cash_api.deletecash

import com.example.cash_api.model.DeleteCashResponse
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.Path

interface DeleteCashApi {
    @DELETE("asset/cash/{id}")
    suspend fun deleteCash(@Path("id") id: String): DeleteCashResponse
}
