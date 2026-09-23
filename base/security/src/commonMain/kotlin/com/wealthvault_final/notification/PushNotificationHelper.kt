package com.wealthvault.push

import com.wealthvault.domain.auth.PushNotificationProvider
import com.wealthvault.push.model.DeviceTokenInfo

/** Compatibility facade implemented by Android Firebase and iOS APNs adapters. */
interface PushNotificationHelper : PushNotificationProvider {
    override fun getDeviceTokenInfo(
        onSuccess: (DeviceTokenInfo) -> Unit,
        onError: (String) -> Unit
    )
}
