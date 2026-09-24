package com.wealthvault.data.portfolio.repository.insurance

import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.cache.FeatureCache
import com.wealthvault.domain.portfolio.InsuranceData
import com.wealthvault.domain.portfolio.InsuranceRequest
import com.wealthvault.domain.portfolio.UpdateInsuranceRepository
import com.wealthvault.data.portfolio.repository.invalidatePortfolioCache

class InsuranceRepositoryImpl(
    private val networkDataSource: InsuranceNetworkDataSource,
    private val cache: FeatureCache? = null,
) : UpdateInsuranceRepository {
    override suspend fun updateInsurance(id: String, request: InsuranceRequest): AppResult<InsuranceData> {
        return networkDataSource.updateInsurance(id, request)
            .invalidatePortfolioCache(cache)
    }

}
