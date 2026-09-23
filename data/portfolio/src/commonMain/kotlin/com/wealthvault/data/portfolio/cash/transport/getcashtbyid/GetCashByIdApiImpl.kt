package com.wealthvault.data.portfolio.cash.transport.getcashtbyid

import com.wealthvault.data.portfolio.cash.transport.toDomainData
import com.wealthvault.config.Config
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class GetCashByIdApiImpl(private val client: HttpClient) : GetCashByIdApi {
    override suspend fun getCashById(id: String) = client
        .get("${Config.localhost_android}asset/cash/$id")
        .body<com.wealthvault.data.portfolio.cash.transport.model.CashIdResponse>()
        .toDomainData()
}
