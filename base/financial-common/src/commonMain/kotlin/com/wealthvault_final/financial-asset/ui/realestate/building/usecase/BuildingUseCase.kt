package com.wealthvault.`financial-asset`.ui.realestate.building.usecase

import com.wealthvault.core.AppUseCase
import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.observability.AppLogger
import com.wealthvault.core.observability.platformLogger
import com.wealthvault.domain.portfolio.BuildingData
import com.wealthvault.domain.portfolio.BuildingRequest
import com.wealthvault.domain.portfolio.CreateBuildingRepository
import kotlinx.coroutines.CoroutineDispatcher

// domain/usecase/AddBuildingUseCase.kt

class AddBuildingUseCase(
    private val repository: CreateBuildingRepository,
    dispatcher: CoroutineDispatcher,
    private val logger: AppLogger = platformLogger(),
): AppUseCase<BuildingRequest, BuildingData>(dispatcher) {

    override suspend fun execute(parameters: BuildingRequest): AppResult<BuildingData> {
        logger.debug("Create building started")
        return repository.createBuilding(parameters).also { result ->
            when (result) {
                is AppResult.Success -> logger.info("Create building succeeded")
                is AppResult.Failure -> logger.warn("Create building failed")
            }
        }
    }
}
