package com.wealthvault.`financial-asset`.ui.insurance.usecase

import com.wealthvault.core.AppUseCase
import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.observability.AppLogger
import com.wealthvault.core.observability.platformLogger
import com.wealthvault.domain.portfolio.CreateInsuranceRepository
import com.wealthvault.domain.portfolio.InsuranceData
import com.wealthvault.domain.portfolio.InsuranceRequest
import kotlinx.coroutines.CoroutineDispatcher

// domain/usecase/AddInsuranceUseCase.kt

class AddInsuranceUseCase(
    private val repository: CreateInsuranceRepository,
    dispatcher: CoroutineDispatcher,
    private val logger: AppLogger = platformLogger(),
): AppUseCase<InsuranceRequest, InsuranceData>(dispatcher) {

    override suspend fun execute(parameters: InsuranceRequest): AppResult<InsuranceData> {
        logger.debug("Create insurance started")
        return repository.createInsurance(parameters).also { result ->
            when (result) {
                is AppResult.Success -> logger.info("Create insurance succeeded")
                is AppResult.Failure -> logger.warn("Create insurance failed")
            }
        }
    }
}
