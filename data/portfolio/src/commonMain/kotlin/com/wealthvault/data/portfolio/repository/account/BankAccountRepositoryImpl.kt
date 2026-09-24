package com.wealthvault.data.portfolio.repository.account

import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.cache.FeatureCache
import com.wealthvault.domain.portfolio.BankAccountData
import com.wealthvault.domain.portfolio.BankAccountRequest
import com.wealthvault.domain.portfolio.UpdateBankAccountRepository
import com.wealthvault.data.portfolio.repository.invalidatePortfolioCache

class BankAccountRepositoryImpl(
    private val networkDataSource: BankAccountNetworkDataSource,
    private val cache: FeatureCache? = null,
) : UpdateBankAccountRepository {
    override suspend fun updateAccount(id: String, request: BankAccountRequest): AppResult<BankAccountData> {
        return networkDataSource.updateAccount(id, request).invalidatePortfolioCache(cache)
    }

}
