package com.wealthvault.liability_api.getliability

import com.wealthvault.config.Config
import com.wealthvault.liability_api.requireDomainData
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class GetLiabilityApiImpl(private val client: HttpClient) : GetLiabilityApi {
    override suspend fun getLiability() = client
        .get("${Config.localhost_android}lia/")
        .body<com.wealthvault.liability_api.model.GetLiabilityResponse>()
        .requireDomainData()
}
