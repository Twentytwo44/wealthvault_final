package com.wealthvault.data.portfolio.repository.debt

import com.wealthvault.domain.portfolio.LiabilityRequest
import com.wealthvault.data.portfolio.liability.transport.updateliability.UpdateLiabilityApi
import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.architecture.runSuspendAppCatching
import com.wealthvault.domain.portfolio.LiabilityData

class LiabilityNetworkDataSource(
    private val createLiabilityApi: UpdateLiabilityApi,
) {
    suspend fun updateLiability(id: String, request: LiabilityRequest): AppResult<LiabilityData> {
        return runSuspendAppCatching {
            createLiabilityApi.updateLiability(id, request)
        }
    }
}
