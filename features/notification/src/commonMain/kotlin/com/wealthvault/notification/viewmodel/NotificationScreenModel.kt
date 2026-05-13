package com.wealthvault.notification.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.wealthvault.core.FlowResult
import com.wealthvault.notification.data.friend.AcceptFriendRepositoryImpl
import com.wealthvault.notification.data.notification.PutNotificationRepositoryImpl
import com.wealthvault.notification.usecase.NotificationUseCase
import com.wealthvault.notification_api.model.NotificationData
import com.wealthvault.`user-api`.model.AcceptFriendRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NotificationScreenModel(
    private val notificationUseCase: NotificationUseCase,
    private val putNotificationRepository: PutNotificationRepositoryImpl,
    private val acceptFriendRepository: AcceptFriendRepositoryImpl
): ScreenModel {

    private val _notificationData = MutableStateFlow<List<NotificationData>>(emptyList())
    val notificationData = _notificationData.asStateFlow()


    // 🌟 2. ฟังก์ชันสำหรับเรียก UseCase เพื่อดึงข้อมูล (Fetch)
    fun fetchNotifications() {
        screenModelScope.launch {
            // 🚨 เรียก UseCase ตรงๆ แบบนี้ได้เลยครับ (มันจะไปเรียก .invoke() ให้เองอัตโนมัติ)
            notificationUseCase(Unit).collect { result ->
                when (result) {
                    is FlowResult.Continue -> {
                        // ✅ ดึงข้อมูลสำเร็จ! เอาข้อมูลยัดใส่ StateFlow ให้ UI เอาไปโชว์ได้เลย
                        _notificationData.value = result.data // (หรือ result.value ขึ้นอยู่กับโค้ดของคุณ)
                    }
                    is FlowResult.Failure -> {
                        // ❌ ดึงข้อมูลไม่สำเร็จ
                        println("🚨 Fetch Notification failed!: ${result.cause}")
                    }

                    else -> {}
                }
            }
        }
    }

    fun readNotification(id: String) {
        screenModelScope.launch {
            // 💡 แก้คอมเมนต์ให้ตรงกับฟังก์ชัน: สั่งอัปเดตสถานะการอ่าน
            val result = putNotificationRepository.putNoti(id)

            result.onSuccess {
                println("✅ Read Notification success!")

                // 🌟 ออปชันเสริม: พออ่านเสร็จ อาจจะเรียก fetchNotifications() อีกรอบเพื่อให้ UI อัปเดตสถานะว่า "อ่านแล้ว" ทันที
                // fetchNotifications()

            }.onFailure { error ->
                println("🚨 Read Notification failed!: ${error.message}")
            }
        }
    }

    fun acceptFriend(id:String,action:String) {

        val request = AcceptFriendRequest(
            requesterId = id,
            action = action
        )
        screenModelScope.launch {
            // 💡 แก้คอมเมนต์: สั่งยอมรับแอดเพื่อน
            val result = acceptFriendRepository.acceptFriend(request)

            result.onSuccess {
                println("✅ Accept Friend success!")

                // 🌟 ออปชันเสริม: กดรับเพื่อนเสร็จ ก็ดึงข้อมูลแจ้งเตือนใหม่ เพื่อให้รายการที่กดรับแล้วหายไปจากหน้าจอ
                // fetchNotifications()

            }.onFailure { error ->
                println("🚨 Accept Friend failed!: ${error.message}")
            }
        }
    }

    fun markAllAsReadBackground() {
        screenModelScope.launch {
            // 1. เช็กก่อนว่ามีอันที่ยังไม่อ่านไหม (ถ้าอ่านหมดแล้วจะได้ไม่ต้องยิง API ให้เปลือง)
            val hasUnread = _notificationData.value.any { it.isRead != true }
            if (!hasUnread) return@launch

            // 2. ยิง API ไปบอกหลังบ้านว่าอ่านหมดแล้ว
            val result = putNotificationRepository.putReadAllNoti()

            result.onSuccess {
                println("✅ Read ALL Notifications (Background) success!")
            }.onFailure { error ->
                println("🚨 Read ALL Background failed!: ${error.message}")
            }
        }
    }
}
