package com.wealthvault.cash_api.deletecash

import com.wealthvault.cash_api.requireSuccess
import com.wealthvault.config.Config
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete

class DeleteCashApiImpl(private val client: HttpClient) : DeleteCashApi {

    override suspend fun deleteCash(id: String) {
        client.delete("${Config.localhost_android}asset/cash/$id/") {}
            .body<com.wealthvault.cash_api.model.DeleteCashResponse>()
            .requireSuccess()
    }
}
