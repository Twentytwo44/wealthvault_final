package com.wealthvault_final.notification.model

data class DeviceTokenInfo(
    val fcmToken: String,
    val platform: String,   // จะส่งเป็น "Android" หรือ "iOS"
    val deviceName: String  // เช่น "Samsung SM-S911B" หรือ "iPhone 15"
)
