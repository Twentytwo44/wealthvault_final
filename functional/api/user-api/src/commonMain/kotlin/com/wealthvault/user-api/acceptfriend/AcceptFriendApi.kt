package com.wealthvault.`user-api`.acceptfriend

import com.wealthvault.`user-api`.model.AcceptFriendRequest
import com.wealthvault.`user-api`.model.AcceptFriendResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST

interface AcceptFriendApi {
    @POST("friend/accept")
    suspend fun acceptFriend(@Body request: AcceptFriendRequest): AcceptFriendResponse
}
