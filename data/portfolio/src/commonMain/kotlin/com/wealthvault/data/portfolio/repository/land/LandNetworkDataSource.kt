package com.wealthvault.data.portfolio.repository.land

import com.wealthvault.domain.portfolio.LandRequest
import com.wealthvault.domain.portfolio.LandData
import com.wealthvault.data.portfolio.land.transport.updateland.UpdateLandApi
import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.architecture.runSuspendAppCatching


class LandNetworkDataSource(
    private val updateLandApi: UpdateLandApi,
) {
    suspend fun updateLand(id:String,request: LandRequest): AppResult<LandData> {
        return runSuspendAppCatching {
            updateLandApi.updateLand(id, request)
        }
    }
}
