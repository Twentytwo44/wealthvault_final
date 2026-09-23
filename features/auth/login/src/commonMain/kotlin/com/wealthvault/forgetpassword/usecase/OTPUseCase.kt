package com.wealthvault.forgetpassword.usecase

import com.wealthvault.domain.auth.OtpVerificationRequest
import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.AppUseCase
import com.wealthvault.core.architecture.toThrowable
import com.wealthvault.core.observability.AppLogger
import com.wealthvault.core.observability.NoOpAppLogger
import com.wealthvault.domain.auth.OtpRepository
// ลบ import io.ktor.http.parameters ทิ้งไปแล้ว!
import kotlinx.coroutines.CoroutineDispatcher

class OTPUseCase(
    private val otpRepository: OtpRepository,
    dispatcher: CoroutineDispatcher,
    private val logger: AppLogger = NoOpAppLogger,
): AppUseCase<OtpVerificationRequest, String>(dispatcher) {

    suspend fun verify(parameters: OtpVerificationRequest): AppResult<String> = invoke(parameters)

    override suspend fun execute(parameters: OtpVerificationRequest): AppResult<String> {
        logger.debug("Password recovery OTP verification started")
        return otpRepository.verify(parameters).also { result ->
            when (result) {
                is AppResult.Success -> logger.info("Password recovery OTP verified")
                is AppResult.Failure -> logger.warn("Password recovery OTP verification failed", result.error.toThrowable())
            }
        }
    }
}
