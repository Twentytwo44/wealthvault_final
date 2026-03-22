package com.wealthvault_final.notification

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MessagingService : FirebaseMessagingService() {

    // ทำงานเมื่อ Token มีการเปลี่ยนแปลงหรือเพิ่งติดตั้งแอป
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // TODO: ส่ง Token ใหม่นี้ไปอัปเดตที่ Django
        println("New FCM Token: $token")
    }

    // ทำงานเมื่อมี Push Notification ส่งมาตอนเปิดแอปอยู่ (Foreground)
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        println("Received message: ${message.notification?.title}")
    }
}
