package com.wealthvault.notification.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.architecture.UiState
import com.wealthvault.core.architecture.toThrowable
import com.wealthvault.core.architecture.toAppError
import com.wealthvault.core.observability.AppLogger
import com.wealthvault.core.observability.NoOpAppLogger
import com.wealthvault.domain.notification.NotificationMutationRepository
import com.wealthvault.domain.notification.NotificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.Job
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

class NotificationScreenModel(
    private val notificationRepository: NotificationRepository,
    private val putNotificationRepository: NotificationMutationRepository,
    private val logger: AppLogger = NoOpAppLogger,
): ScreenModel {

    private val _uiState = MutableStateFlow(NotificationUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()
    val appUiState: StateFlow<UiState<NotificationUiState>> = _uiState
        .map { state ->
            UiState(
                data = state,
                isLoading = state.isLoading,
                isRefreshing = state.isRefreshing,
                isStale = state.freshness != com.wealthvault.core.architecture.CacheFreshness.Fresh,
                error = state.error,
            )
        }
        .stateIn(
            screenModelScope,
            SharingStarted.Eagerly,
            UiState(data = NotificationUiState(isLoading = true), isLoading = true),
        )

    private val _effects = MutableSharedFlow<NotificationUiEffect>(extraBufferCapacity = 1)
    val effects = _effects.asSharedFlow()

    private var fetchJob: Job? = null
    private var markAllJob: Job? = null
    private val readingNotificationIds = mutableSetOf<String>()

    init {
        fetchNotifications()
    }

    fun onAction(action: NotificationUiAction) {
        when (action) {
            NotificationUiAction.Refresh -> fetchNotifications(forceRefresh = true)
        }
    }

    fun fetchNotifications(forceRefresh: Boolean = false) {
        // A manual refresh must not overlap the initial load. The repository
        // already owns stale-while-revalidate and single-flight behavior; the
        // screen model only needs one active UI request at a time.
        if (fetchJob?.isActive == true) return
        val job = screenModelScope.launch {
            try {
                _uiState.update {
                    it.copy(
                        isLoading = true,
                        isRefreshing = forceRefresh,
                        error = null,
                    )
                }
                when (val result = notificationRepository.getNoti(forceRefresh)) {
                    is AppResult.Success -> {
                        _uiState.value = _uiState.value.copy(
                            items = result.value.value,
                            isLoading = false,
                            isRefreshing = false,
                            freshness = result.value.freshness,
                            error = null,
                        )
                    }
                    is AppResult.Failure -> {
                        logger.warn("Fetch notifications failed", result.error.toThrowable())
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isRefreshing = false,
                            error = result.error,
                        )
                        _effects.tryEmit(NotificationUiEffect.ShowError(result.error))
                    }
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                val appError = error.toAppError()
                logger.warn("Fetch notifications failed unexpectedly", error)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isRefreshing = false,
                    error = appError,
                )
                _effects.tryEmit(NotificationUiEffect.ShowError(appError))
            }
        }
        fetchJob = job
        job.invokeOnCompletion { if (fetchJob === job) fetchJob = null }
    }

    fun readNotification(id: String) {
        if (!readingNotificationIds.add(id)) return
        screenModelScope.launch {
            try {
                val result = putNotificationRepository.markRead(id)
                when (result) {
                    is AppResult.Success -> {
                        logger.debug("Notification marked as read")
                        _uiState.update { state ->
                            state.copy(items = state.items.map { item ->
                                if (item.id == id) item.copy(isRead = true) else item
                            })
                        }
                    }
                    is AppResult.Failure -> {
                        logger.warn("Mark notification as read failed", result.error.toThrowable())
                    }
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                logger.warn("Mark notification as read failed unexpectedly", error)
            } finally {
                readingNotificationIds.remove(id)
            }
        }
    }

    fun markAllAsReadBackground() {
        if (markAllJob?.isActive == true) return
        val job = screenModelScope.launch {
            try {
                val hasUnread = _uiState.value.items.any { it.isRead != true }
                if (!hasUnread) return@launch

                val result = putNotificationRepository.markAllRead()

                when (result) {
                    is AppResult.Success -> {
                        logger.debug("All notifications marked as read")
                        _uiState.update { state ->
                            state.copy(items = state.items.map { item -> item.copy(isRead = true) })
                        }
                    }
                    is AppResult.Failure -> {
                        logger.warn("Mark all notifications as read failed", result.error.toThrowable())
                    }
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                logger.warn("Mark all notifications as read failed unexpectedly", error)
            }
        }
        markAllJob = job
        job.invokeOnCompletion { if (markAllJob === job) markAllJob = null }
    }

}
