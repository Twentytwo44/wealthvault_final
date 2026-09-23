package com.wealthvault.data.portfolio.land.transport.getlandbyid

import com.wealthvault.domain.portfolio.LandIdData

interface GetLandByIdApi {
    suspend fun getLandById(id: String): LandIdData?
}
