package com.wealthvault.data.portfolio.liability.transport.getliability

import com.wealthvault.domain.portfolio.GetLiabilityData

interface GetLiabilityApi {
    suspend fun getLiability(): List<GetLiabilityData>
}
