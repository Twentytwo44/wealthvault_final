package com.wealthvault.data.portfolio.form.asset.data.building

import com.wealthvault.data.portfolio.building.transport.getbuilding.GetBuildingApi
import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.architecture.runSuspendAppCatching
import com.wealthvault.domain.portfolio.GetBuildingData


class GetBuildingNetworkDataSource(
    private val getBuildingApi: GetBuildingApi,
) {
    suspend fun getBuilding(): AppResult<List<GetBuildingData>> {
        return runSuspendAppCatching {
            getBuildingApi.getBuilding()
        }
    }
}
