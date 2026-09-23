package com.wealthvault.data.portfolio.investment.transport.createcash


import com.wealthvault.domain.portfolio.BuildingData
import com.wealthvault.domain.portfolio.BuildingRequest

interface CreateBuildingApi {
    suspend fun create(request: BuildingRequest): BuildingData
}
