package com.wealthvault.login.usecase

import com.wealthvault.core.AppUseCase
import com.wealthvault.core.observability.AppLogger
import com.wealthvault.core.observability.NoOpAppLogger
import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.architecture.toThrowable
import com.wealthvault.domain.auth.AuthRepository
import com.wealthvault.domain.auth.AuthenticatedSession
import com.wealthvault.domain.auth.LoginCredentials
import kotlinx.coroutines.CoroutineDispatcher

class LoginUseCase(
    private val authRepository: AuthRepository,

    // 1. รับ dispatcher เพิ่มเข้ามา
    dispatcher: CoroutineDispatcher,
    private val logger: AppLogger = NoOpAppLogger,
): AppUseCase<LoginCredentials, AuthenticatedSession>(dispatcher) {

    /** Named entry point kept for readable presentation call sites. */
    suspend fun login(parameters: LoginCredentials): AppResult<AuthenticatedSession> = invoke(parameters)

    override suspend fun execute(parameters: LoginCredentials): AppResult<AuthenticatedSession> {
        logger.debug("Login started")
        return authRepository.login(parameters).also { result ->
            when (result) {
                is AppResult.Success -> logger.info("Login succeeded")
                is AppResult.Failure -> logger.warn("Login failed", result.error.toThrowable())
            }
        }
    }
}
