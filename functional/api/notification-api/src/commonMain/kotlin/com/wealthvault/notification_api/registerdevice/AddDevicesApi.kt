package com.wealthvault.notification_api.registerdevice

import com.wealthvault.notification_api.model.DeviceRequest
import com.wealthvault.notification_api.model.DeviceResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST

interface AddDevicesApi {
    @POST("devices/register/")
    suspend fun addDevices(@Body request: DeviceRequest): DeviceResponse
}
