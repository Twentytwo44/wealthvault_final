package com.wealthvault.push

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.wealthvault.core.observability.platformLogger

class MessagingService : FirebaseMessagingService() {

    private val logger = platformLogger()

    // ทำงานเมื่อ Token มีการเปลี่ยนแปลงหรือเพิ่งติดตั้งแอป
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // TODO: ส่ง Token ใหม่นี้ไปอัปเดตที่ Django
        logger.info("FCM token refreshed")
    }

    // ทำงานเมื่อมี Push Notification ส่งมาตอนเปิดแอปอยู่ (Foreground)
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        logger.info("Foreground push message received")
    }
}
