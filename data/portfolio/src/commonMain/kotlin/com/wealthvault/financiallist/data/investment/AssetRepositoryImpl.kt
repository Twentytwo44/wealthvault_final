package com.wealthvault.data.portfolio.repository.investment

import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.cache.FeatureCache
import com.wealthvault.domain.portfolio.InvestmentData
import com.wealthvault.domain.portfolio.InvestmentRequest
import com.wealthvault.domain.portfolio.UpdateInvestmentRepository
import com.wealthvault.data.portfolio.repository.invalidatePortfolioCache

class AssetRepositoryImpl(
    private val networkDataSource: AssetNetworkDataSource,
    private val cache: FeatureCache? = null,
) : UpdateInvestmentRepository {
    override suspend fun updateInvestment(id: String, request: InvestmentRequest): AppResult<InvestmentData> {
        return networkDataSource.updateInvestment(id, request)
            .invalidatePortfolioCache(cache)
    }

}
