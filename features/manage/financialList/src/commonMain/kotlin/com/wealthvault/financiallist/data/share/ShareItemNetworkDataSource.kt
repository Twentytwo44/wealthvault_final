package com.wealthvault.financiallist.data.share

import com.wealthvault.share_api.model.ShareItemRequest
import com.wealthvault.share_api.shareitem.ShareItemApi

class ShareItemNetworkDataSource(
    private val shareItemApi: ShareItemApi,
) {
    suspend fun shareItem(request: ShareItemRequest): Result<String> {
        return runCatching {
            val result = shareItemApi.shareItem(request)
            print("share item result:"+ result)
            result.status ?: throw IllegalArgumentException(result.error ?: "Unknown Error")        }
    }
}
