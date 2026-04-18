package com.wealthvault.share_api.getitemtosharegroup

import com.wealthvault.config.Config
import com.wealthvault.share_api.getitemtoshare.GetItemToShareApi
import com.wealthvault.share_api.model.ItemToShareResponse
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.contentType

class GetItemToShareApiImpl(private val ktorfit: Ktorfit) : GetItemToShareApi {
    override suspend fun getItemsToShare(type: String, id: String): ItemToShareResponse {
        val client = ktorfit.httpClient

        val url = "${Config.localhost_android}share/$type/$id/selection"
        println("🚀 API Request URL: $url")
        // 🌟 เปลี่ยน URL ตาม type ที่ส่งเข้ามา
        return client.get("${Config.localhost_android}share/$type/$id/selection") {
            contentType(ContentType.Application.Json)
        }.body()
    }
}