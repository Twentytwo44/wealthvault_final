package com.wealthvault.`user-api`.pendingfriend

import com.wealthvault.domain.social.PendingFriend

interface PendingFriendApi {
    suspend fun pendingFriend(): List<PendingFriend>
}
