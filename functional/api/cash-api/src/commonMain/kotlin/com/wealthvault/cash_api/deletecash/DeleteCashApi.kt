package com.wealthvault.cash_api.deletecash

import com.wealthvault.cash_api.model.DeleteCashResponse
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.Path

interface DeleteCashApi {
    @DELETE("ic_nav_asset/cash/{id}")
    suspend fun deleteCash(@Path("id") id: String): DeleteCashResponse
}
