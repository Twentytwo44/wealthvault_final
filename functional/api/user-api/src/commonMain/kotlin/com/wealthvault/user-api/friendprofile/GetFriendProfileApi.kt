package com.wealthvault.`user-api`.friendprofile

import com.wealthvault.`user-api`.model.FriendProfileResponse

interface GetFriendProfileApi {
    suspend fun getFriendProfile(id: String): FriendProfileResponse
}