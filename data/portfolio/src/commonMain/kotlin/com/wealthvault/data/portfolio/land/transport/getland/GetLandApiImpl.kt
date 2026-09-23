package com.wealthvault.data.portfolio.land.transport.getland

import com.wealthvault.config.Config
import com.wealthvault.data.portfolio.land.transport.model.GetLandResponse
import com.wealthvault.data.portfolio.land.transport.requireDomainData
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class GetLandApiImpl(private val client: HttpClient) : GetLandApi {
    override suspend fun getLand() = client
        .get("${Config.localhost_android}asset/land/")
        .body<GetLandResponse>()
        .requireDomainData()
}
