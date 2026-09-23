package com.wealthvault.`user-api`.updateclosefriend

interface UpdateCloseFriendApi {
    suspend fun updateCloseFriend(
        friendId: String,
        isClose: Boolean
    ): Boolean
}
