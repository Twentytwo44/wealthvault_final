package com.wealthvault.notification_api.unregisterdevice


import com.wealthvault.config.Config
import com.wealthvault.notification_api.model.DeviceResponse
import com.wealthvault.notification_api.model.UnDeviceRequest
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class UnDevicesApiImpl(private val ktorfit: Ktorfit) : UnDevicesApi {
    override suspend fun unDevices(request: UnDeviceRequest): DeviceResponse {
        // ใช้ HttpClient ที่อยู่ใน Ktorfit ส่งค่าออกไปจริงๆ
        val client = ktorfit.httpClient

        return client.post("${Config.localhost_android}devices/unregister/") {
            setBody(request)
        }.body()
    }
}
