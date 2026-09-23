package com.wealthvault.data.portfolio.investment.transport.createcash


import com.wealthvault.domain.portfolio.LandData
import com.wealthvault.domain.portfolio.LandRequest

interface CreateLandApi {
    suspend fun create(request: LandRequest): LandData
}
