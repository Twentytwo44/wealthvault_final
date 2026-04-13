package com.wealthvault.notification_api.getalldevice

import com.wealthvault.notification_api.model.GetDeviceResponse
import de.jensklingenberg.ktorfit.http.GET

interface GetAllDeviceApi {
    @GET("devices")
    suspend fun getAllDevices(): GetDeviceResponse
}
