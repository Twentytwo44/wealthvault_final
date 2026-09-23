package com.wealthvault.data.portfolio.liability.transport.getliabilitybyid

import com.wealthvault.config.Config
import com.wealthvault.data.portfolio.liability.transport.toDomainData
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class GetLiabilityByIdApiImpl(private val client: HttpClient) : GetLiabilityByIdApi {
    override suspend fun getLiabilityById(id: String) = client
        .get("${Config.localhost_android}lia/$id/")
        .body<com.wealthvault.data.portfolio.liability.transport.model.LiabilityIdResponse>()
        .toDomainData()
}
