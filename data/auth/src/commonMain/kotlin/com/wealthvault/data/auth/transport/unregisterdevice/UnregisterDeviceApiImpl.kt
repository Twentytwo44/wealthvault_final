package com.wealthvault.data.auth.transport.unregisterdevice

import com.wealthvault.config.Config
import com.wealthvault.data.auth.transport.model.DeviceMutationResponse
import com.wealthvault.data.auth.transport.model.UnregisterDeviceRequest
import com.wealthvault.data.auth.wire.toDomain
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody

internal class UnregisterDeviceApiImpl(private val client: HttpClient) : UnregisterDeviceApi {
    override suspend fun unregisterDevice(token: String) =
        client.post("${Config.localhost_android}devices/unregister/") {
            setBody(UnregisterDeviceRequest(token = token))
        }.body<DeviceMutationResponse>().toDomain()
}
