package com.wealthvault.liability_api.getliabilitybyid

import com.wealthvault.domain.portfolio.LiabilityIdData

interface GetLiabilityByIdApi {
    suspend fun getLiabilityById(id: String): LiabilityIdData?
}
