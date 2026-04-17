package com.wealthvault.financiallist.data.share

import com.wealthvault.share_api.model.ItemShareTargetsResponse

class ShareTargetsRepositoryImpl(
    private val networkDataSource: ShareTargetsNetworkDatasource,
) {
    suspend fun shareTargets(id:String,type:String): Result<ItemShareTargetsResponse> {
        return networkDataSource.shareTargets(id,type).map { data ->
            data
        }
    }

}
