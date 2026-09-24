package com.wealthvault.app.navigation

import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.architecture.toThrowable
import com.wealthvault.core.observability.AppLogger
import com.wealthvault.domain.auth.DeviceRegistration
import com.wealthvault.domain.auth.PushDeviceRepository
import com.wealthvault.domain.auth.PushDeviceToken
import com.wealthvault.domain.auth.PushNotificationProvider
import com.wealthvault.domain.auth.SessionDeviceInfo
import com.wealthvault.domain.auth.SessionManager
import com.wealthvault.domain.auth.SessionState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume

/**
 * Registers the current push token for an authenticated process session.
 *
 * This belongs to the composition root rather than the login screen: routing
 * may dispose the login ScreenModel immediately after secure tokens are saved,
 * while an existing session also needs registration on a cold app start.
 */
internal class SessionDeviceRegistrar(
    private val provider: PushNotificationProvider,
    private val repository: PushDeviceRepository,
    private val sessionManager: SessionManager,
    private val logger: AppLogger,
    private val tokenTimeoutMillis: Long = DEFAULT_TOKEN_TIMEOUT_MS,
) {
    suspend fun registerCurrentDevice() {
        try {
            val device = withTimeoutOrNull(tokenTimeoutMillis) {
                awaitDeviceToken()
            }
            if (device == null) {
                logger.warn("Push device token was unavailable during session registration")
                return
            }

            onDeviceTokenChanged(device)
        } catch (error: CancellationException) {
            throw error
        } catch (error: Throwable) {
            // Push registration is best effort and must never block an
            // otherwise valid authenticated session from opening the app.
            logger.warn("Push device registration failed unexpectedly", error)
        }
    }

    /**
     * Persists a platform token refresh immediately and submits it only while
     * the secure session is authenticated. A token can arrive before login;
     * the next authenticated coordinator pass reads the persisted platform
     * value and retries registration.
     */
    suspend fun onDeviceTokenChanged(device: PushDeviceToken) {
        sessionManager.saveDeviceInfo(
            SessionDeviceInfo(
                fcmToken = device.fcmToken,
                platform = device.platform,
                deviceName = device.deviceName,
            ),
        )

        // The session may have been cleared while the platform SDK was
        // resolving its token. Never attach a device to a signed-out session.
        if (sessionManager.status.value != SessionState.Authenticated) return

        when (
            val result = repository.register(
                DeviceRegistration(
                    token = device.fcmToken,
                    platform = device.platform,
                    deviceName = device.deviceName,
                ),
            )
        ) {
            is AppResult.Success -> logger.info("Push device registration succeeded")
            is AppResult.Failure -> logger.warn(
                "Push device registration failed",
                result.error.toThrowable(),
            )
        }
    }

    private suspend fun awaitDeviceToken(): PushDeviceToken? =
        suspendCancellableCoroutine { continuation ->
            provider.getDeviceTokenInfo(
                onSuccess = { device ->
                    if (continuation.isActive) continuation.resume(device)
                },
                onError = { message ->
                    logger.warn("Unable to read push device information", IllegalStateException(message))
                    if (continuation.isActive) continuation.resume(null)
                },
            )
        }

    private companion object {
        const val DEFAULT_TOKEN_TIMEOUT_MS = 5_000L
    }
}
