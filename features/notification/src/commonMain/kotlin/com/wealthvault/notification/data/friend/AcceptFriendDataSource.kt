package com.wealthvault.notification.data.friend

import com.wealthvault.`user-api`.acceptfriend.AcceptFriendApi
import com.wealthvault.`user-api`.model.AcceptFriendData
import com.wealthvault.`user-api`.model.AcceptFriendRequest

class AcceptFriendDataSource(
    private val acceptFriendApi: AcceptFriendApi
) {
    suspend fun acceptFriend(request: AcceptFriendRequest): Result<AcceptFriendData> {
        return runCatching {
            val result = acceptFriendApi.acceptFriend(request)
            println("Notification Result: ${result.data}")
            result.data ?: error("Error: Put notification failed")
        }
    }
}
