package com.wealthvault.profile.ui

import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.architecture.toAppError
import com.wealthvault.core.architecture.toThrowable
import com.wealthvault.core.observability.AppLogger
import com.wealthvault.domain.auth.SessionManager
import com.wealthvault.domain.profile.DeviceRegistrationRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

/**
 * Coordinates remote device cleanup with local secure logout.
 *
 * Remote unregister is best effort: an offline backend must never trap the
 * user inside an authenticated local session. Local clearing runs in a
 * non-cancellable section so disposing the profile screen cannot interrupt
 * deletion of access and refresh tokens.
 */
internal class SessionLogoutCoordinator(
    private val deviceRepository: DeviceRegistrationRepository,
    private val sessionManager: SessionManager,
    private val logger: AppLogger,
) {
    suspend fun logout(): AppResult<Unit> {
        var cancellation: CancellationException? = null

        try {
            val token = withTimeoutOrNull(TOKEN_READ_TIMEOUT_MS) {
                sessionManager.fcmToken.firstOrNull()
            }.orEmpty()
            when (
                val result = withTimeoutOrNull(UNREGISTER_TIMEOUT_MS) {
                    deviceRepository.unregister(token)
                }
            ) {
                is AppResult.Success -> logger.info("Unregister device succeeded")
                is AppResult.Failure -> logger.warn(
                    "Unregister device failed",
                    result.error.toThrowable(),
                )
                null -> logger.warn("Unregister device timed out")
            }
        } catch (error: CancellationException) {
            // Preserve structured cancellation after the secure session has
            // been cleared in the non-cancellable block below.
            cancellation = error
        } catch (error: Throwable) {
            logger.warn("Unregister device failed unexpectedly", error)
        }

        val clearResult = try {
            withContext(NonCancellable) {
                sessionManager.clear()
            }
            AppResult.Success(Unit)
        } catch (error: Throwable) {
            logger.warn("Local session logout failed", error)
            AppResult.Failure(error.toAppError())
        }

        cancellation?.let { throw it }
        return clearResult
    }

    private companion object {
        const val TOKEN_READ_TIMEOUT_MS = 2_000L
        const val UNREGISTER_TIMEOUT_MS = 10_000L
    }
}
