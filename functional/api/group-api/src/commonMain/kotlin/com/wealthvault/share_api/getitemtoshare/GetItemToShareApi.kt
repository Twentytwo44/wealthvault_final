package com.wealthvault.share_api.getitemtoshare

import com.wealthvault.domain.social.ShareableItem

interface GetItemToShareApi {
    // 🌟 ใช้ {type} เพื่อรองรับทั้ง "group" และ "friend" ในที่เดียว
    suspend fun getItemsToShare(
        type: String, // ส่ง "group" หรือ "friend"
        id: String
    ): List<ShareableItem>
}
