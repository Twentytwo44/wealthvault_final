package com.wealthvault.data.portfolio.repository.building

import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.cache.FeatureCache
import com.wealthvault.domain.portfolio.BuildingData
import com.wealthvault.domain.portfolio.BuildingRequest
import com.wealthvault.domain.portfolio.UpdateBuildingRepository
import com.wealthvault.data.portfolio.repository.invalidatePortfolioCache


class BuildingRepositoryImpl(
    private val networkDataSource: BuildingNetworkDataSource,
    private val cache: FeatureCache? = null,
) : UpdateBuildingRepository {
    override suspend fun updateBuilding(id: String, request: BuildingRequest): AppResult<BuildingData> {
        return networkDataSource.updateBuilding(id, request)
            .invalidatePortfolioCache(cache)
    }

}
