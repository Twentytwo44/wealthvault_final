package com.wealthvault.`financial-asset`.ui.realestate.land.usecase

import com.wealthvault.core.AppUseCase
import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.observability.AppLogger
import com.wealthvault.core.observability.platformLogger
import com.wealthvault.domain.portfolio.CreateLandRepository
import com.wealthvault.domain.portfolio.LandData
import com.wealthvault.domain.portfolio.LandRequest
import kotlinx.coroutines.CoroutineDispatcher

// domain/usecase/AddLandUseCase.kt

class AddLandUseCase(
    private val repository: CreateLandRepository,
    dispatcher: CoroutineDispatcher,
    private val logger: AppLogger = platformLogger(),
): AppUseCase<LandRequest, LandData>(dispatcher) {

    override suspend fun execute(parameters: LandRequest): AppResult<LandData> {
        logger.debug("Create land started")
        return repository.create(parameters).also { result ->
            when (result) {
                is AppResult.Success -> logger.info("Create land succeeded")
                is AppResult.Failure -> logger.warn("Create land failed")
            }
        }
    }
}
