package com.wealthvault.notification.data.friend

import com.wealthvault.`user-api`.model.AcceptFriendData
import com.wealthvault.`user-api`.model.AcceptFriendRequest

class AcceptFriendRepositoryImpl(
    private val networkDataSource: AcceptFriendDataSource,
) {
    suspend fun acceptFriend(request: AcceptFriendRequest): Result<AcceptFriendData> {
        return networkDataSource.acceptFriend(request)
    }

}
