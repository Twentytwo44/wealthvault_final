package com.wealthvault.`financial-asset`.ui.stock.usecase

import com.wealthvault.core.AppUseCase
import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.observability.AppLogger
import com.wealthvault.core.observability.platformLogger
import com.wealthvault.domain.portfolio.CreateInvestmentRepository
import com.wealthvault.domain.portfolio.InvestmentData
import com.wealthvault.domain.portfolio.InvestmentRequest
import kotlinx.coroutines.CoroutineDispatcher

// domain/usecase/AddStockUseCase.kt

class AddStockUseCase(
    private val repository: CreateInvestmentRepository,
    dispatcher: CoroutineDispatcher,
    private val logger: AppLogger = platformLogger(),
): AppUseCase<InvestmentRequest, InvestmentData>(dispatcher) {

    override suspend fun execute(parameters: InvestmentRequest): AppResult<InvestmentData> {
        logger.debug("Create investment started")
        return repository.create(parameters).also { result ->
            when (result) {
                is AppResult.Success -> logger.info("Create investment succeeded")
                is AppResult.Failure -> logger.warn("Create investment failed")
            }
        }
    }
}
