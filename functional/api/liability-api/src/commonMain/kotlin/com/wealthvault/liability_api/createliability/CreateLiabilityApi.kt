package com.wealthvault.investment_api.createcash


import com.wealthvault.domain.portfolio.LiabilityData
import com.wealthvault.domain.portfolio.LiabilityRequest

interface CreateLiabilityApi {
    suspend fun create(request: LiabilityRequest): LiabilityData
}
