package com.wealthvault.data.portfolio.form.asset.data.bankaccount

import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.cache.FeatureCache
import com.wealthvault.domain.portfolio.BankAccountData
import com.wealthvault.domain.portfolio.BankAccountRequest
import com.wealthvault.domain.portfolio.CreateBankAccountRepository
import com.wealthvault.data.portfolio.repository.invalidatePortfolioCache

class BankAccountRepositoryImpl(
    private val networkDataSource: BankAccountNetworkDataSource,
    private val cache: FeatureCache? = null,
) : CreateBankAccountRepository {
    override suspend fun createBankAccount(request: BankAccountRequest): AppResult<BankAccountData> {
        return networkDataSource.createBankAccount(request).invalidatePortfolioCache(cache)
    }

}
