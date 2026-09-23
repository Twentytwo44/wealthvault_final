package com.wealthvault.data.portfolio.form.asset.data.insurance

import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.cache.FeatureCache
import com.wealthvault.domain.portfolio.InsuranceData
import com.wealthvault.domain.portfolio.InsuranceRequest
import com.wealthvault.domain.portfolio.CreateInsuranceRepository
import com.wealthvault.data.portfolio.repository.invalidatePortfolioCache

class InsuranceRepositoryImpl(
    private val networkDataSource: InsuranceNetworkDataSource,
    private val cache: FeatureCache? = null,
) : CreateInsuranceRepository {
    override suspend fun createInsurance(request: InsuranceRequest): AppResult<InsuranceData> {
        return networkDataSource.createInsurance(request).invalidatePortfolioCache(cache)
    }

}
