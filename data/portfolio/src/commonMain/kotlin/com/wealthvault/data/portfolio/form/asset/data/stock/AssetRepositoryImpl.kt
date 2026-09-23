package com.wealthvault.data.portfolio.form.asset.data.stock

import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.cache.FeatureCache
import com.wealthvault.domain.portfolio.InvestmentData
import com.wealthvault.domain.portfolio.InvestmentRequest
import com.wealthvault.domain.portfolio.CreateInvestmentRepository
import com.wealthvault.data.portfolio.repository.invalidatePortfolioCache

class AssetRepositoryImpl(
    private val networkDataSource: AssetNetworkDataSource,
    private val cache: FeatureCache? = null,
) : CreateInvestmentRepository {
    override suspend fun create(request: InvestmentRequest): AppResult<InvestmentData> {
        return networkDataSource.create(request).invalidatePortfolioCache(cache)
    }

}
