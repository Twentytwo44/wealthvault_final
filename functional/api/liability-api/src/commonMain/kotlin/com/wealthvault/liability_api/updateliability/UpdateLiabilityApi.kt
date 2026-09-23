package com.wealthvault.liability_api.updateliability



import com.wealthvault.domain.portfolio.LiabilityData
import com.wealthvault.domain.portfolio.LiabilityRequest

interface UpdateLiabilityApi {
    suspend fun updateLiability(id: String, request: LiabilityRequest): LiabilityData
}
