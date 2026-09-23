package com.wealthvault.data.portfolio.investment.transport.getinvestment

import com.wealthvault.config.Config
import com.wealthvault.data.portfolio.investment.transport.requireDomainData
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class GetInvestmentApiImpl(private val client: HttpClient) : GetInvestmentApi {
    override suspend fun getInvestment() = client
        .get("${Config.localhost_android}asset/invest")
        .body<com.wealthvault.data.portfolio.investment.transport.model.GetInvestmentResponse>()
        .requireDomainData()
}
