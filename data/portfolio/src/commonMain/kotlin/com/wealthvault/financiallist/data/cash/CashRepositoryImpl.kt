package com.wealthvault.data.portfolio.repository.cash

import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.cache.FeatureCache
import com.wealthvault.domain.portfolio.CashData
import com.wealthvault.domain.portfolio.CashRequest
import com.wealthvault.domain.portfolio.UpdateCashRepository
import com.wealthvault.data.portfolio.repository.invalidatePortfolioCache

class CashRepositoryImpl(
    private val networkDataSource: CashNetworkDataSource,
    private val cache: FeatureCache? = null,
) : UpdateCashRepository {
    override suspend fun updateCash(id: String, request: CashRequest): AppResult<CashData> {
        return networkDataSource.updateCash(id, request)
            .invalidatePortfolioCache(cache)
    }

}
