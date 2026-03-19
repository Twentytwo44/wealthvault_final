package com.wealthvault.`user-api`.pendingfriend

import com.wealthvault.`user-api`.model.PendingFriendResponse
import de.jensklingenberg.ktorfit.http.GET

interface PendingFriendApi {
    @GET("friend/pending")
    suspend fun pendingFriend(): PendingFriendResponse
}
