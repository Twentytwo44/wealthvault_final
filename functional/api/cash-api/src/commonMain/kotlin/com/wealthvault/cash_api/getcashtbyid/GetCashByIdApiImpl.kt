package com.wealthvault.cash_api.getcashtbyid

import com.wealthvault.cash_api.toDomainData
import com.wealthvault.config.Config
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class GetCashByIdApiImpl(private val client: HttpClient) : GetCashByIdApi {
    override suspend fun getCashById(id: String) = client
        .get("${Config.localhost_android}asset/cash/$id")
        .body<com.wealthvault.cash_api.model.CashIdResponse>()
        .toDomainData()
}
