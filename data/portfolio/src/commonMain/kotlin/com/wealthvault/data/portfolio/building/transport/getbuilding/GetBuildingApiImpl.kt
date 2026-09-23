package com.wealthvault.data.portfolio.building.transport.getbuilding

import com.wealthvault.data.portfolio.building.transport.model.GetBuildingResponse
import com.wealthvault.data.portfolio.building.transport.requireDomainData
import com.wealthvault.config.Config
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class GetBuildingApiImpl(private val client: HttpClient) : GetBuildingApi {
    override suspend fun getBuilding() = client
        .get("${Config.localhost_android}asset/building/")
        .body<GetBuildingResponse>()
        .requireDomainData()
}
