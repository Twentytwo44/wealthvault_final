package com.wealthvault.data.portfolio.form.asset.data.cash

import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.cache.FeatureCache
import com.wealthvault.domain.portfolio.CashData
import com.wealthvault.domain.portfolio.CashRequest
import com.wealthvault.domain.portfolio.CreateCashRepository
import com.wealthvault.data.portfolio.repository.invalidatePortfolioCache

class CashRepositoryImpl(
    private val networkDataSource: CashNetworkDataSource,
    private val cache: FeatureCache? = null,
) : CreateCashRepository {
    override suspend fun create(request: CashRequest): AppResult<CashData> {
        return networkDataSource.create(request).invalidatePortfolioCache(cache)
    }

}
