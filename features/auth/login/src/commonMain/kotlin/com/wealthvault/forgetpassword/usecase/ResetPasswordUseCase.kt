package com.wealthvault.forgetpassword.usecase

import com.wealthvault.domain.auth.PasswordResetRequest
import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.AppUseCase
import com.wealthvault.core.architecture.toThrowable
import com.wealthvault.core.observability.AppLogger
import com.wealthvault.core.observability.NoOpAppLogger
import com.wealthvault.domain.auth.PasswordResetRepository
import kotlinx.coroutines.CoroutineDispatcher


class ResetPasswordUseCase(
    private val resetRepository: PasswordResetRepository,
    // 1. รับ dispatcher เพิ่มเข้ามา
    dispatcher: CoroutineDispatcher,
    private val logger: AppLogger = NoOpAppLogger,
): AppUseCase<PasswordResetRequest, Unit>(dispatcher) {

    suspend fun resetPassword(parameters: PasswordResetRequest): AppResult<Unit> = invoke(parameters)

    override suspend fun execute(parameters: PasswordResetRequest): AppResult<Unit> {
        logger.debug("Password reset started")
        return resetRepository.reset(parameters).also { result ->
            when (result) {
                is AppResult.Success -> logger.info("Password reset succeeded")
                is AppResult.Failure -> logger.warn("Password reset failed", result.error.toThrowable())
            }
        }
    }
}
