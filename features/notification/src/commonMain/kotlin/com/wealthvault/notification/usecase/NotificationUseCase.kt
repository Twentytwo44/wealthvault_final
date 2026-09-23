package com.wealthvault.notification.usecase

import com.wealthvault.core.AppUseCase
import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.architecture.toThrowable
import com.wealthvault.domain.notification.NotificationRepository
import com.wealthvault.domain.notification.NotificationSnapshot
import com.wealthvault.core.observability.AppLogger
import com.wealthvault.core.observability.NoOpAppLogger
import kotlinx.coroutines.CoroutineDispatcher

class NotificationUseCase(
    private val notificationRepository: NotificationRepository,
    dispatcher: CoroutineDispatcher,
    private val logger: AppLogger = NoOpAppLogger,
): AppUseCase<Unit, NotificationSnapshot>(dispatcher) {

    override suspend fun execute(parameters: Unit): AppResult<NotificationSnapshot> {
        val result = notificationRepository.getNoti()
        when (result) {
            is AppResult.Success -> logger.debug("Notification list loaded: ${result.value.value.size} items")
            is AppResult.Failure -> logger.warn("Notification list request failed", result.error.toThrowable())
        }
        return result
    }
}
