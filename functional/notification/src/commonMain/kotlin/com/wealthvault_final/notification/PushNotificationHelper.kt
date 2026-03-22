package com.wealthvault_final.notification

import com.wealthvault_final.notification.model.DeviceTokenInfo

interface PushNotificationHelper {
    fun getDeviceTokenInfo(
        onSuccess: (DeviceTokenInfo) -> Unit,
        onError: (String) -> Unit
    )
}
