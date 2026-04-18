package com.wealthvault.financiallist.data.friend

import com.wealthvault.`user-api`.model.FriendData

class FriendRepositoryImpl(
    private val networkDataSource: FriendNetworkDataSource,
) {
    suspend fun getFriend(): Result<List<FriendData>> {
        return networkDataSource.getFriend()
    }
}
