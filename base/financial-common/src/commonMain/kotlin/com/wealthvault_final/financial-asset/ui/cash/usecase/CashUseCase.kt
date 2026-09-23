package com.wealthvault.`financial-asset`.ui.cash.usecase

import com.wealthvault.core.AppUseCase
import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.observability.AppLogger
import com.wealthvault.core.observability.platformLogger
import com.wealthvault.domain.portfolio.CashData
import com.wealthvault.domain.portfolio.CashRequest
import com.wealthvault.domain.portfolio.CreateCashRepository
import kotlinx.coroutines.CoroutineDispatcher

// domain/usecase/AddStockUseCase.kt

class AddCashUseCase(
    private val repository: CreateCashRepository,
    dispatcher: CoroutineDispatcher,
    private val logger: AppLogger = platformLogger(),
): AppUseCase<CashRequest, CashData>(dispatcher) {

    override suspend fun execute(parameters: CashRequest): AppResult<CashData> {
        logger.debug("Create cash started")
        return repository.create(parameters).also { result ->
            when (result) {
                is AppResult.Success -> logger.info("Create cash succeeded")
                is AppResult.Failure -> logger.warn("Create cash failed")
            }
        }
    }
}
