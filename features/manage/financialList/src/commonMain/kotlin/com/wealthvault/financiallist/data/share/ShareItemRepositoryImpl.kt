package com.wealthvault.financiallist.data.share

import com.wealthvault.share_api.model.ShareItemRequest

class ShareItemRepositoryImpl(
    private val networkDataSource: ShareItemNetworkDataSource,
) {
    suspend fun shareItem(request: ShareItemRequest): Result<String> {
        return networkDataSource.shareItem(request).map { data ->
                data
        }
    }

}
