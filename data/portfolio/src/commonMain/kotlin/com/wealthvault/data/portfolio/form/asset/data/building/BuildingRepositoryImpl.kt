package com.wealthvault.data.portfolio.form.asset.data.building

import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.cache.FeatureCache
import com.wealthvault.domain.portfolio.BuildingData
import com.wealthvault.domain.portfolio.BuildingRequest
import com.wealthvault.domain.portfolio.CreateBuildingRepository
import com.wealthvault.data.portfolio.repository.invalidatePortfolioCache


class BuildingRepositoryImpl(
    private val networkDataSource: BuildingNetworkDataSource,
    private val cache: FeatureCache? = null,
) : CreateBuildingRepository {
    override suspend fun createBuilding(request: BuildingRequest): AppResult<BuildingData> {
        return networkDataSource.createBuilding(request).invalidatePortfolioCache(cache)
    }

}
