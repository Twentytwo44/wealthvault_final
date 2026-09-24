package com.wealthvault.data.portfolio.repository.debt

import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.cache.FeatureCache
import com.wealthvault.domain.portfolio.LiabilityData
import com.wealthvault.domain.portfolio.LiabilityRequest
import com.wealthvault.domain.portfolio.UpdateLiabilityRepository
import com.wealthvault.data.portfolio.repository.invalidatePortfolioCache

class LiabilityRepositoryImpl(
    private val networkDataSource: LiabilityNetworkDataSource,
    private val cache: FeatureCache? = null,
) : UpdateLiabilityRepository {
    override suspend fun updateLiability(id: String, request: LiabilityRequest): AppResult<LiabilityData> {
        return networkDataSource.updateLiability(id, request)
            .invalidatePortfolioCache(cache)
    }

}
