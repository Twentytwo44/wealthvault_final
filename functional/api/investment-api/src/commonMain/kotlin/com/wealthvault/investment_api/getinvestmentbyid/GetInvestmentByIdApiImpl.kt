package com.wealthvault.investment_api.getinvestmentbyid

import com.wealthvault.config.Config
import com.wealthvault.investment_api.toDomainData
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class GetInvestmentByIdApiImpl(private val client: HttpClient) : GetInvestmentByIdApi {
    override suspend fun getInvestmentById(id: String) = client
        .get("${Config.localhost_android}asset/invest/$id")
        .body<com.wealthvault.investment_api.model.InvestmentIdResponse>()
        .toDomainData()
}
