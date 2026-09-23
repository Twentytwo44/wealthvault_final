package com.wealthvault.building_api.getbuildingbyid

import com.wealthvault.building_api.model.BuildingIdResponse
import com.wealthvault.building_api.toDomainData
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
