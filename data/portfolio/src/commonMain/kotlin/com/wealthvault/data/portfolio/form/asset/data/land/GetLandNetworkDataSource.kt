package com.wealthvault.data.portfolio.form.asset.data.land

import com.wealthvault.data.portfolio.land.transport.getland.GetLandApi
import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.architecture.runSuspendAppCatching
import com.wealthvault.domain.portfolio.GetLandData


class GetLandNetworkDataSource(
    private val getLandApi: GetLandApi,
) {
    suspend fun getLand(): AppResult<List<GetLandData>> {
        return runSuspendAppCatching {
            getLandApi.getLand()
        }
    }
}
