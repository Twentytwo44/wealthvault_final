package com.wealthvault.data.portfolio.building.transport.getbuildingbyid

import com.wealthvault.data.portfolio.building.transport.model.BuildingIdResponse
import com.wealthvault.data.portfolio.building.transport.toDomainData
import com.wealthvault.config.Config
import com.wealthvault.domain.portfolio.BuildingIdData
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class GetBuildingByIdApiImpl(private val client: HttpClient) : GetBuildingByIdApi {
    override suspend fun getBuildingById(id: String): BuildingIdData? = client
        .get("${Config.localhost_android}asset/building/${id}/")
        .body<BuildingIdResponse>()
        .toDomainData()
}
