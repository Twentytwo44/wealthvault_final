package com.wealthvault.`financial-obligations`.ui.expense.usecase


import com.wealthvault.core.AppUseCase
import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.observability.AppLogger
import com.wealthvault.core.observability.platformLogger
import com.wealthvault.domain.portfolio.CreateLiabilityRepository
import com.wealthvault.domain.portfolio.LiabilityData
import com.wealthvault.domain.portfolio.LiabilityRequest
import kotlinx.coroutines.CoroutineDispatcher

class AddExpenseUseCase(
    private val repository: CreateLiabilityRepository,
    dispatcher: CoroutineDispatcher,
    private val logger: AppLogger = platformLogger(),
): AppUseCase<LiabilityRequest, LiabilityData>(dispatcher) {

    override suspend fun execute(parameters: LiabilityRequest): AppResult<LiabilityData> {
        logger.debug("Create expense started")
        return repository.createLiability(parameters).also { result ->
            when (result) {
                is AppResult.Success -> logger.info("Create expense succeeded")
                is AppResult.Failure -> logger.warn("Create expense failed")
            }
        }
    }
}
