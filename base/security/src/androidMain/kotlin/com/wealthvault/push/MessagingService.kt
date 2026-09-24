package com.wealthvault.push

import android.os.Build
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.wealthvault.core.observability.platformLogger
import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.architecture.toThrowable
import com.wealthvault.domain.auth.DeviceRegistration
import com.wealthvault.domain.auth.PushDeviceRepository
import com.wealthvault.domain.auth.SessionDeviceInfo
import com.wealthvault.domain.auth.SessionManager
import com.wealthvault.domain.auth.SessionState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.koin.core.context.GlobalContext

class MessagingService : FirebaseMessagingService() {

    private val logger = platformLogger()
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // ทำงานเมื่อ Token มีการเปลี่ยนแปลงหรือเพิ่งติดตั้งแอป
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        logger.info("FCM token refreshed")
        serviceScope.launch {
            try {
                val koin = GlobalContext.get()
                val sessionManager = koin.get<SessionManager>()
                val deviceName = buildDeviceName()
                val device = SessionDeviceInfo(
                    fcmToken = token,
                    platform = "Android",
                    deviceName = deviceName,
                )
                sessionManager.saveDeviceInfo(device)

                // A signed-out device should keep the token locally; the
                // composition-root registrar submits it after authentication.
                if (sessionManager.status.value == SessionState.Authenticated) {
                    when (
                        val result = koin.get<PushDeviceRepository>().register(
                            DeviceRegistration(
                                token = token,
                                platform = "Android",
                                deviceName = deviceName,
                            ),
                        )
                    ) {
                        is AppResult.Success -> logger.info("FCM token registration succeeded")
                        is AppResult.Failure -> logger.warn(
                            "FCM token registration rejected",
                            result.error.toThrowable(),
                        )
                    }
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                // Token refresh must never crash Firebase's service process.
                // The next authenticated app start retries registration.
                logger.warn("FCM token registration failed", error)
            }
        }
    }

    // ทำงานเมื่อมี Push Notification ส่งมาตอนเปิดแอปอยู่ (Foreground)
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        logger.info("Foreground push message received")
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }

    private fun buildDeviceName(): String {
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        return if (model.lowercase().startsWith(manufacturer.lowercase())) {
            model.replaceFirstChar { it.uppercase() }
        } else {
            "${manufacturer.replaceFirstChar { it.uppercase() }} $model"
        }
    }
}
