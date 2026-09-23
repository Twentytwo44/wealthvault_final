package com.wealthvault.register.usecase

import com.wealthvault.core.AppUseCase
import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.architecture.toThrowable
import com.wealthvault.core.observability.AppLogger
import com.wealthvault.core.observability.NoOpAppLogger
import com.wealthvault.domain.auth.RegistrationCredentials
import com.wealthvault.domain.auth.RegistrationRepository
import kotlinx.coroutines.CoroutineDispatcher


class RegisterUseCase(
    private val registerRepository: RegistrationRepository,
    // 1. รับ dispatcher เพิ่มเข้ามา
    dispatcher: CoroutineDispatcher,
    private val logger: AppLogger = NoOpAppLogger,
): AppUseCase<RegistrationCredentials, Unit>(dispatcher) {

    /** AppResult-first entry point for new presentation code. */
    suspend fun register(parameters: RegistrationCredentials): AppResult<Unit> = invoke(parameters)

    override suspend fun execute(parameters: RegistrationCredentials): AppResult<Unit> {
        logger.debug("Registration started")
        return registerRepository.register(parameters).also { result ->
            when (result) {
                is AppResult.Success -> logger.info("Registration succeeded")
                is AppResult.Failure -> logger.warn("Registration failed", result.error.toThrowable())
            }
        }
    }
}
