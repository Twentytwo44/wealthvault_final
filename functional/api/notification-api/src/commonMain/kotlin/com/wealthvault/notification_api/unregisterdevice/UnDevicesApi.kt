package com.wealthvault.investment_api.createcash

import com.wealthvault.notification_api.model.DeviceResponse
import com.wealthvault.notification_api.model.UnDeviceRequest
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST

interface UnDevicesApi {
    @POST("devices/unregister/")
    suspend fun unDevices(@Body request: UnDeviceRequest): DeviceResponse
}
