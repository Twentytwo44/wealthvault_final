package com.wealthvault.share_api.getitemtoshare

import com.wealthvault.share_api.model.ItemToShareResponse
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path

interface GetItemToShareApi {
    // 🌟 ใช้ {type} เพื่อรองรับทั้ง "group" และ "friend" ในที่เดียว
    @GET("share/{type}/{id}/selection")
    suspend fun getItemsToShare(
        @Path("type") type: String, // ส่ง "group" หรือ "friend"
        @Path("id") id: String
    ): ItemToShareResponse
}