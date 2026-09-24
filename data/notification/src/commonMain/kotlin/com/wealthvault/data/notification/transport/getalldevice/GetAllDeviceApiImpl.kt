package com.wealthvault.data.notification.transport.getalldevice

import com.wealthvault.config.Config
import com.wealthvault.data.notification.transport.toDomain
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class GetAllDeviceApiImpl(private val client: HttpClient) : GetAllDeviceApi {
    override suspend fun getAllDevices(): List<com.wealthvault.domain.notification.DeviceInfo> =
        client.get("${Config.apiBaseUrl}devices/") {

        }.body<com.wealthvault.data.notification.transport.model.GetDeviceResponse>().toDomain()
}
