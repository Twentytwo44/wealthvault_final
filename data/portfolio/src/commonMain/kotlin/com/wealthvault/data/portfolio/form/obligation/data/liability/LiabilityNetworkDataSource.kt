package com.wealthvault.data.portfolio.form.obligation.data.liability

import com.wealthvault.data.portfolio.investment.transport.createcash.CreateLiabilityApi
import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.architecture.runSuspendAppCatching
import com.wealthvault.domain.portfolio.LiabilityData
import com.wealthvault.domain.portfolio.LiabilityRequest

class LiabilityNetworkDataSource(
    private val createLiabilityApi: CreateLiabilityApi,
) {
    suspend fun createLiability(request: LiabilityRequest): AppResult<LiabilityData> {
        return runSuspendAppCatching {
            createLiabilityApi.create(request)
        }
    }
}
