package com.wealthvault.data.portfolio.repository.building

import com.wealthvault.domain.portfolio.BuildingData
import com.wealthvault.domain.portfolio.BuildingRequest
import com.wealthvault.data.portfolio.building.transport.updatebuilding.UpdateBuildingApi
import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.architecture.runSuspendAppCatching

class BuildingNetworkDataSource(
    private val updateBuildingApi: UpdateBuildingApi,
) {
    suspend fun updateBuilding(id: String, request: BuildingRequest): AppResult<BuildingData> {
        return runSuspendAppCatching {
            updateBuildingApi.updateBuilding(id, request)
        }
    }
}
