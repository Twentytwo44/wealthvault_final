package com.wealthvault.notification.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.core.FlowResult
import com.wealthvault.notification.data.notification.PutNotificationRepositoryImpl
import com.wealthvault.notification.usecase.NotificationUseCase
import com.wealthvault.notification_api.model.NotificationData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob

class NotificationScreenModel(
    private val notificationUseCase: NotificationUseCase,
    private val putNotificationRepository: PutNotificationRepositoryImpl
): ScreenModel {

    private val _notificationData = MutableStateFlow<List<NotificationData>>(emptyList())
    val notificationData = _notificationData.asStateFlow()

    // 🌟 เพิ่ม State ควบคุม Loading
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    fun fetchNotifications() {
        screenModelScope.launch {
            _isLoading.value = true
            notificationUseCase(Unit).collect { result ->
                when (result) {
                    is FlowResult.Continue -> {
                        _notificationData.value = result.data
                    }
                    is FlowResult.Failure -> {
                        println("🚨 Fetch Notification failed!: ${result.cause}")
                    }
                    else -> {}
                }
                _isLoading.value = false
            }
        }
    }

    fun readNotification(id: String) {
        screenModelScope.launch {
            val result = putNotificationRepository.putNoti(id)
            result.onSuccess {
                println("✅ Read Notification success!")
            }.onFailure { error ->
                println("🚨 Read Notification failed!: ${error.message}")
            }
        }
    }

    fun markAllAsReadBackground() {
        // 🌟 เปลี่ยนจาก screenModelScope.launch มาใช้ CoroutineScope อิสระ
        // เพื่อให้มันทำงานจนจบได้ แม้ว่าผู้ใช้จะกด Back ออกจากหน้าจอไปแล้ว
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {

            val hasUnread = _notificationData.value.any { it.isRead != true }
            if (!hasUnread) return@launch

            val result = putNotificationRepository.putReadAllNoti()

            result.onSuccess {
                println("✅ Read ALL Notifications (Background) success!")
            }.onFailure { error ->
                println("🚨 Read ALL Background failed!: ${error.message}")
            }
        }
    }
}