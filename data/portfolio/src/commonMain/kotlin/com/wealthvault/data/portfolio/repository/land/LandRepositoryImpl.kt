package com.wealthvault.data.portfolio.repository.land


import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.cache.FeatureCache
import com.wealthvault.domain.portfolio.LandData
import com.wealthvault.domain.portfolio.LandRequest
import com.wealthvault.domain.portfolio.UpdateLandRepository
import com.wealthvault.data.portfolio.repository.invalidatePortfolioCache

class LandRepositoryImpl(
    private val networkDataSource: LandNetworkDataSource,
    private val cache: FeatureCache? = null,
) : UpdateLandRepository {
    override suspend fun updateLand(id: String, request: LandRequest): AppResult<LandData> {
        return networkDataSource.updateLand(id, request)
            .invalidatePortfolioCache(cache)
    }

}
