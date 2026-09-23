package com.wealthvault.`user-api`.addfriend

interface AddFriendApi {
    suspend fun addFriend(requesterId: String): Boolean
}
