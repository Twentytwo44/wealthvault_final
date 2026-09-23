package com.wealthvault.share_api.getitemtosharegroup

import com.wealthvault.config.Config
import com.wealthvault.share_api.getitemtoshare.GetItemToShareApi
import com.wealthvault.share_api.requireDomainData
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.contentType

class GetItemToShareApiImpl(private val client: HttpClient) : GetItemToShareApi {
    override suspend fun getItemsToShare(type: String, id: String) = client
        // 🌟 เปลี่ยน URL ตาม type ที่ส่งเข้ามา
        .get("${Config.localhost_android}share/$type/$id/selection") {
            contentType(ContentType.Application.Json)
        }.body<com.wealthvault.share_api.model.ItemToShareResponse>().requireDomainData()
}
