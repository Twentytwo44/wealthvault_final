package com.wealthvault.data.portfolio.land.transport.updateland

import com.wealthvault.domain.portfolio.LandData
import com.wealthvault.domain.portfolio.LandRequest

interface UpdateLandApi {
    suspend fun updateLand(id: String, request: LandRequest): LandData
}
