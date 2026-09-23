package com.wealthvault.data.portfolio.investment.transport.createcash


import com.wealthvault.domain.portfolio.LiabilityData
import com.wealthvault.domain.portfolio.LiabilityRequest

interface CreateLiabilityApi {
    suspend fun create(request: LiabilityRequest): LiabilityData
}
