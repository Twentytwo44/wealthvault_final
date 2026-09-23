package com.wealthvault.login

import com.wealthvault.core.architecture.AppError
import com.wealthvault.core.architecture.AppResult
import com.wealthvault.domain.auth.AuthRepository
import com.wealthvault.domain.auth.AuthenticatedSession
import com.wealthvault.domain.auth.LoginCredentials
import com.wealthvault.login.usecase.LoginUseCase
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class LoginTest {
    @Test
    fun successfulLoginReturnsSession() = runTest {
        val useCase = LoginUseCase(
            authRepository = FakeAuthRepository(AppResult.Success(session())),
            dispatcher = StandardTestDispatcher(testScheduler),
        )

        val result = useCase(LoginCredentials("user@example.com", "password"))

        assertEquals("user-1", assertIs<AppResult.Success<AuthenticatedSession>>(result).value.userId)
    }

    @Test
    fun failedLoginExposesAppErrorAsFailure() = runTest {
        val useCase = LoginUseCase(
            authRepository = FakeAuthRepository(AppResult.Failure(AppError.Unauthorized)),
            dispatcher = StandardTestDispatcher(testScheduler),
        )

        val result = useCase(LoginCredentials("user@example.com", "wrong"))
        val failure = assertIs<AppResult.Failure>(result)

        assertEquals(AppError.Unauthorized, failure.error)
    }

    private class FakeAuthRepository(
        private val result: AppResult<AuthenticatedSession>,
    ) : AuthRepository {
        override suspend fun login(credentials: LoginCredentials): AppResult<AuthenticatedSession> = result
    }

    private fun session() = AuthenticatedSession(
        userId = "user-1",
        accessToken = "access-token",
        refreshToken = "refresh-token",
    )
}
