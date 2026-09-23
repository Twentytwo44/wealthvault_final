package com.wealthvault.data.portfolio.liability.transport.getliabilitybyid

import com.wealthvault.domain.portfolio.LiabilityIdData

interface GetLiabilityByIdApi {
    suspend fun getLiabilityById(id: String): LiabilityIdData?
}
