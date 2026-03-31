package com.wealthvault.`user-api`.updateclosefriend

import com.wealthvault.`user-api`.model.UpdateCloseFriendResponse

interface UpdateCloseFriendApi {
    suspend fun updateCloseFriend(
        friendId: String,
        isClose: Boolean
    ): UpdateCloseFriendResponse
}