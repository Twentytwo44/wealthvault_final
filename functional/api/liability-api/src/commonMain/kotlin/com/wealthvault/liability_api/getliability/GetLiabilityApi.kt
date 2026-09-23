package com.wealthvault.liability_api.getliability

import com.wealthvault.domain.portfolio.GetLiabilityData

interface GetLiabilityApi {
    suspend fun getLiability(): List<GetLiabilityData>
}
