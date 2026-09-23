package com.wealthvault.data.portfolio.cash.transport.getcash

import com.wealthvault.data.portfolio.cash.transport.requireDomainData
import com.wealthvault.config.Config
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class GetCashApiImpl(private val client: HttpClient) : GetCashApi {
    override suspend fun getCash() = client.get("${Config.localhost_android}asset/cash") {
    }.body<com.wealthvault.data.portfolio.cash.transport.model.GetCashResponse>().requireDomainData()
}
