package com.wealthvault.data.portfolio.form.asset.data.land


import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.cache.FeatureCache
import com.wealthvault.domain.portfolio.LandData
import com.wealthvault.domain.portfolio.LandRequest
import com.wealthvault.domain.portfolio.CreateLandRepository
import com.wealthvault.data.portfolio.repository.invalidatePortfolioCache

class LandRepositoryImpl(
    private val networkDataSource: LandNetworkDataSource,
    private val cache: FeatureCache? = null,
) : CreateLandRepository {
    override suspend fun create(request: LandRequest): AppResult<LandData> {
        return networkDataSource.create(request).invalidatePortfolioCache(cache)
    }

}
