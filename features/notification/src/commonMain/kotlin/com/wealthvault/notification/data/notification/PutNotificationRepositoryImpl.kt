package com.wealthvault.notification.data.notification

import com.wealthvault.notification_api.model.NotificationResponse

class PutNotificationRepositoryImpl(
    private val networkDataSource: PutNotificationDataSource,
) {
    suspend fun putNoti(id:String): Result<NotificationResponse> {
        return networkDataSource.putNoti(id)
    }

    // 🌟 เพิ่มฟังก์ชันให้ ScreenModel มาเรียกใช้งาน
    suspend fun putReadAllNoti(): Result<NotificationResponse> {
        return networkDataSource.putReadAllNoti()
    }
}