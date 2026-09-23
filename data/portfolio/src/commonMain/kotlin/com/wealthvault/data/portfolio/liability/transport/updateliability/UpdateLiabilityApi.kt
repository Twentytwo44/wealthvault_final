package com.wealthvault.data.portfolio.liability.transport.updateliability



import com.wealthvault.domain.portfolio.LiabilityData
import com.wealthvault.domain.portfolio.LiabilityRequest

interface UpdateLiabilityApi {
    suspend fun updateLiability(id: String, request: LiabilityRequest): LiabilityData
}
