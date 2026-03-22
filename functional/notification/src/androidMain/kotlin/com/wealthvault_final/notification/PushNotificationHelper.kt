package com.wealthvault_final.notification

import android.os.Build
import com.google.firebase.messaging.FirebaseMessaging
import com.wealthvault_final.notification.model.DeviceTokenInfo

class AndroidPushNotificationHelper : PushNotificationHelper {

    override fun getDeviceTokenInfo(onSuccess: (DeviceTokenInfo) -> Unit, onError: (String) -> Unit) {
        // 1. ดึง FCM Token
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                onError(task.exception?.message ?: "Failed to get FCM token")
                return@addOnCompleteListener
            }

            val fcmToken = task.result ?: ""

            // 2. ดึงชื่อรุ่นและยี่ห้อมือถือ
            val manufacturer = Build.MANUFACTURER
            val model = Build.MODEL

            // จัดฟอร์แมตชื่อเครื่องให้สวยงาม (เผื่อบางยี่ห้อใส่ชื่อซ้ำ)
            val deviceName = if (model.lowercase().startsWith(manufacturer.lowercase())) {
                model.replaceFirstChar { it.uppercase() }
            } else {
                "${manufacturer.replaceFirstChar { it.uppercase() }} $model"
            }

            // 3. ประกอบร่างข้อมูลทั้ง 3 ส่วน
            val tokenInfo = DeviceTokenInfo(
                fcmToken = fcmToken,
                platform = "Android", // ฝั่งนี้ Hardcode เป็น Android ได้เลย
                deviceName = deviceName
            )

            print(tokenInfo)

            // ส่งกลับไปให้ KMP ใช้งาน
            onSuccess(tokenInfo)
        }
    }
}
