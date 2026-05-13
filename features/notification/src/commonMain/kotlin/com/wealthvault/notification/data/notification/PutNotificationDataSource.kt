package com.wealthvault.notification.data.notification

import com.wealthvault.notification_api.model.NotificationResponse
import com.wealthvault.notification_api.read.PutNotiApi
import com.wealthvault.notification_api.readall.PutNotiReadAllApi // 🌟 Import API ใหม่

class PutNotificationDataSource(
    private val putNotificationApi: PutNotiApi,
    private val putNotiReadAllApi: PutNotiReadAllApi // 🌟 ฉีด API สำหรับ Read All เข้ามา
) {
    suspend fun putNoti(id:String): Result<NotificationResponse> {
        return runCatching {
            val result = putNotificationApi.putNoti(id)
            println("Notification Result: ${result.data}")
            result ?: error("Error: Put notification failed")
        }
    }

    // 🌟 เพิ่มฟังก์ชันสำหรับอ่านทั้งหมด
    suspend fun putReadAllNoti(): Result<NotificationResponse> {
        return runCatching {
            val result = putNotiReadAllApi.putNotiReadAll()
            println("Read All Notification Result: ${result.data}")
            result ?: error("Error: Put read all notification failed")
        }
    }
}