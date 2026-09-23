package com.wealthvault.forgetpassword.usecase

import com.wealthvault.domain.auth.PasswordRecoveryRequest
import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.AppUseCase
import com.wealthvault.core.architecture.toThrowable
import com.wealthvault.core.observability.AppLogger
import com.wealthvault.core.observability.NoOpAppLogger
import com.wealthvault.domain.auth.PasswordRecoveryRepository
import kotlinx.coroutines.CoroutineDispatcher


class ForgetUsecase(
    private val forgetRepository: PasswordRecoveryRepository,
    // 1. รับ dispatcher เพิ่มเข้ามา
    dispatcher: CoroutineDispatcher,
    private val logger: AppLogger = NoOpAppLogger,
): AppUseCase<PasswordRecoveryRequest, Unit>(dispatcher) {

    suspend fun requestOtp(parameters: PasswordRecoveryRequest): AppResult<Unit> = invoke(parameters)

    override suspend fun execute(parameters: PasswordRecoveryRequest): AppResult<Unit> {
        logger.debug("Password recovery OTP requested")
        return forgetRepository.requestOtp(parameters).also { result ->
            when (result) {
                is AppResult.Success -> logger.info("Password recovery OTP sent")
                is AppResult.Failure -> logger.warn("Password recovery OTP failed", result.error.toThrowable())
            }
        }
    }
}
