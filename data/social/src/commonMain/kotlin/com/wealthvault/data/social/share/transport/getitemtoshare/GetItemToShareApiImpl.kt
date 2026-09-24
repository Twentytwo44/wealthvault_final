package com.wealthvault.data.social.share.transport.getitemtosharegroup

import com.wealthvault.config.Config
import com.wealthvault.data.social.share.transport.getitemtoshare.GetItemToShareApi
import com.wealthvault.data.social.share.transport.requireDomainData
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
        }.body<com.wealthvault.data.social.share.transport.model.ItemToShareResponse>().requireDomainData()
}
