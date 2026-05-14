package com.wealthvault.dashboard.ui

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.core.FlowResult // 🌟 อย่าลืมเช็ค Import ตัวนี้
import com.wealthvault.dashboard.data.DashboardRepositoryImpl
import com.wealthvault.notification.usecase.NotificationUseCase // 🌟 Import UseCase จากฝั่ง Notification
import com.wealthvault.`user-api`.model.DashboardDataResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DashboardScreenModel(
    private val repository: DashboardRepositoryImpl,
    private val notificationUseCase: NotificationUseCase // 🌟 1. ฉีด UseCase เข้ามาตรงนี้
) : ScreenModel {

    private val _dashboardState = MutableStateFlow<DashboardDataResponse?>(null)
    val dashboardState = _dashboardState.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    // 🌟 2. เพิ่ม State สำหรับเก็บสถานะจุดแดง (แก้เส้นแดง _hasUnreadNoti)
    private val _hasUnreadNoti = MutableStateFlow(false)
    val hasUnreadNoti = _hasUnreadNoti.asStateFlow()

    fun fetchDashboard() {
        screenModelScope.launch {
            _isLoading.value = true

            // 🌟 1. สั่งดึง Dashboard ให้เสร็จก่อน
            repository.getDashboardData()
                .onSuccess { data ->
                    _dashboardState.value = data
                }
                .onFailure { error ->
                    println("🚨 Dashboard Error: ${error.message}")
                }

            // 🌟 2. พอ Dashboard ดึงเสร็จ (ไม่ว่าจะสำเร็จหรือพัง) ค่อยเรียกเช็ค Noti ต่อ
            // วิธีนี้จะทำให้เซิร์ฟเวอร์ไม่โดนรุมยิงพร้อมกันครับ
            checkUnreadNotifications()

            _isLoading.value = false
        }
    }

    private fun checkUnreadNotifications() {
        screenModelScope.launch {
            // 🌟 3. เรียกใช้ notificationUseCase (เส้นแดงหายแล้ว)
            notificationUseCase(Unit).collect { result ->
                when (result) {
                    is FlowResult.Continue -> {
                        val hasUnread = result.data.any { it.isRead != true }
                        _hasUnreadNoti.value = hasUnread
                    }
                    else -> {}
                }
            }
        }
    }
}