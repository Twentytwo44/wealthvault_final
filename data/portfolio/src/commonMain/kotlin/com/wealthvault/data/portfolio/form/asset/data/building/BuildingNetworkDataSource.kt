package com.wealthvault.data.portfolio.form.asset.data.building

import com.wealthvault.domain.portfolio.BuildingData
import com.wealthvault.domain.portfolio.BuildingRequest
import com.wealthvault.data.portfolio.investment.transport.createcash.CreateBuildingApi
import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.architecture.runSuspendAppCatching

class BuildingNetworkDataSource(
    private val createBuildingApi: CreateBuildingApi,
) {
    suspend fun createBuilding(request: BuildingRequest): AppResult<BuildingData> {
        return runSuspendAppCatching {
            createBuildingApi.create(request)
        }
    }
}
