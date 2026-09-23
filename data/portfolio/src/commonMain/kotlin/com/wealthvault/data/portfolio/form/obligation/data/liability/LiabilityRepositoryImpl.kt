package com.wealthvault.data.portfolio.form.obligation.data.liability

import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.cache.FeatureCache
import com.wealthvault.domain.portfolio.LiabilityData
import com.wealthvault.domain.portfolio.LiabilityRequest
import com.wealthvault.domain.portfolio.CreateLiabilityRepository
import com.wealthvault.data.portfolio.repository.invalidatePortfolioCache

class LiabilityRepositoryImpl(
    private val networkDataSource: LiabilityNetworkDataSource,
    private val cache: FeatureCache? = null,
) : CreateLiabilityRepository {
    override suspend fun createLiability(request: LiabilityRequest): AppResult<LiabilityData> {
        return networkDataSource.createLiability(request).invalidatePortfolioCache(cache)
    }

}
