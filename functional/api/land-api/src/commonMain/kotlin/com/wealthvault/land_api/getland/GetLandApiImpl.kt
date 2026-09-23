package com.wealthvault.land_api.getland

import com.wealthvault.config.Config
import com.wealthvault.land_api.model.GetLandResponse
import com.wealthvault.land_api.requireDomainData
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class GetLandApiImpl(private val client: HttpClient) : GetLandApi {
    override suspend fun getLand() = client
        .get("${Config.localhost_android}asset/land/")
        .body<GetLandResponse>()
        .requireDomainData()
}
