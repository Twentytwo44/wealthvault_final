package com.wealthvault.`user-api`.deletefriend

interface DeleteFriendApi {
    suspend fun deleteFriend(id: String): Boolean
}
