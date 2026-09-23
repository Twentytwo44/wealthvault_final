package com.wealthvault.notification_api.registerdevice


import com.wealthvault.config.Config
import com.wealthvault.notification_api.model.DeviceRequest
import com.wealthvault.notification_api.toDomain
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class AddDevicesApiImpl(private val client: HttpClient) : AddDevicesApi {
    override suspend fun addDevices(
        token: String?,
        platform: String?,
        deviceName: String?,
    ): com.wealthvault.domain.notification.DeviceMutationResult =
        client.post("${Config.localhost_android}devices/register/") {
            setBody(DeviceRequest(token = token, platform = platform, deviceName = deviceName))
        }.body<com.wealthvault.notification_api.model.DeviceResponse>().toDomain()
}
