package com.wealthvault.financiallist.data.share

import com.wealthvault.share_api.itemsharetargets.GetItemShareTargetsApi
import com.wealthvault.share_api.model.ItemShareTargetsResponse

class ShareTargetsNetworkDatasource(
    private val shareTargetsApi: GetItemShareTargetsApi,
) {
    suspend fun shareTargets(id: String, type:String): Result<ItemShareTargetsResponse> {
        return runCatching {
            val result = shareTargetsApi.getItemShareTargets(type,id)
            print("share item result:"+ result)
            result
        }
    }
}
