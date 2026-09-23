package com.wealthvault.`user-api`.friendprofile

import com.wealthvault.domain.social.FriendProfile

interface GetFriendProfileApi {
    suspend fun getFriendProfile(id: String): FriendProfile
}
