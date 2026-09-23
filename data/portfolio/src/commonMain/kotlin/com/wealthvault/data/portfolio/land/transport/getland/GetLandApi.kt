package com.wealthvault.data.portfolio.land.transport.getland

import com.wealthvault.domain.portfolio.GetLandData

interface GetLandApi {
    suspend fun getLand(): List<GetLandData>
}
