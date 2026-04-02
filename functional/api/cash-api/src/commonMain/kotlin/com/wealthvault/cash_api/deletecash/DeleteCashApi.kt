package com.wealthvault.cash_api.deletecash

import com.wealthvault.core.model.DeleteBaseResponse
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.Path

interface DeleteCashApi {
    @DELETE("asset/cash/{id}")
    suspend fun deleteCash(@Path("id") id: String): DeleteBaseResponse
}
