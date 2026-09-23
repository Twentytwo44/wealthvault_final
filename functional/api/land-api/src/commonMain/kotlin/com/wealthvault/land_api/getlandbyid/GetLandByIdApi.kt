package com.wealthvault.land_api.getlandbyid

import com.wealthvault.domain.portfolio.LandIdData

interface GetLandByIdApi {
    suspend fun getLandById(id: String): LandIdData?
}
