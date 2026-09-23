package com.wealthvault.data.portfolio.form.asset.data.land

import com.wealthvault.data.portfolio.investment.transport.createcash.CreateLandApi
import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.architecture.runSuspendAppCatching
import com.wealthvault.domain.portfolio.LandData
import com.wealthvault.domain.portfolio.LandRequest


class LandNetworkDataSource(
    private val createLandApi: CreateLandApi,
) {
    suspend fun create(request: LandRequest): AppResult<LandData> {
        return runSuspendAppCatching {
            createLandApi.create(request)
        }
    }
}
