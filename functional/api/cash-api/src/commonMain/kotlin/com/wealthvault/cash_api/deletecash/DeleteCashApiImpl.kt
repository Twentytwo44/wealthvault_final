package com.wealthvault.cash_api.deletecash

import com.wealthvault.cash_api.model.DeleteCashResponse
import com.wealthvault.config.Config
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.delete

class DeleteCashApiImpl(private val ktorfit: Ktorfit) : DeleteCashApi {

    override suspend fun deleteCash(id: String): DeleteCashResponse {
        val client = ktorfit.httpClient

        return client.delete("${Config.localhost_android}asset/cash/$id/") {

        }.body()
    }
}
