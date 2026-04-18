package com.wealthvault.financiallist.data.friend

import com.wealthvault.`user-api`.friend.FriendApi
import com.wealthvault.`user-api`.model.FriendData

class FriendNetworkDataSource(
    private val friendApi: FriendApi,
) {
    suspend fun getFriend(): Result<List<FriendData>> {
        return runCatching {
            val result = friendApi.getFriend()
            println("Result Get Friend:"+ result)
            result.data?.friend ?: throw IllegalArgumentException(result.error)
        }
    }
}
