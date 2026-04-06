package com.wealthvault.share_api.shareitem

import com.wealthvault.share_api.model.ShareItemRequest
import com.wealthvault.share_api.model.ShareItemResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST

interface ShareItemApi {
    @POST("share/item/")
    suspend fun shareItem( @Body request: ShareItemRequest): ShareItemResponse
}
