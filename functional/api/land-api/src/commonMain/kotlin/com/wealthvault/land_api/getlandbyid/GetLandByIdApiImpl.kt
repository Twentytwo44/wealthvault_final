package com.wealthvault.land_api.getlandbyid

import com.wealthvault.config.Config
import com.wealthvault.land_api.model.LandIdResponse
import com.wealthvault.land_api.toDomainData
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class GetLandByIdApiImpl(private val client: HttpClient) : GetLandByIdApi {
    override suspend fun getLandById(id: String) = client
        .get("${Config.localhost_android}asset/land/$id/")
        .body<LandIdResponse>()
        .toDomainData()
}
