package com.wealthvault.`user-api`.friend

import com.wealthvault.domain.profile.FriendData

interface FriendApi {
    suspend fun getFriend(): List<FriendData>
}
