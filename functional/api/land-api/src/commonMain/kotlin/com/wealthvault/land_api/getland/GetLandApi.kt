package com.wealthvault.land_api.getland

import com.wealthvault.domain.portfolio.GetLandData

interface GetLandApi {
    suspend fun getLand(): List<GetLandData>
}
