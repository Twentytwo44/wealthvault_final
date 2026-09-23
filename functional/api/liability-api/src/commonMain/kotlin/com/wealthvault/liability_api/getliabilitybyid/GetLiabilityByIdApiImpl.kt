package com.wealthvault.liability_api.getliabilitybyid

import com.wealthvault.config.Config
import com.wealthvault.liability_api.toDomainData
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class GetLiabilityByIdApiImpl(private val client: HttpClient) : GetLiabilityByIdApi {
    override suspend fun getLiabilityById(id: String) = client
        .get("${Config.localhost_android}lia/$id/")
        .body<com.wealthvault.liability_api.model.LiabilityIdResponse>()
        .toDomainData()
}
