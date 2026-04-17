package com.wealthvault.`user-api`.addfriend

import com.wealthvault.`user-api`.model.AcceptFriendRequest
import com.wealthvault.`user-api`.model.AcceptFriendResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST

interface AddFriendApi {
    @POST("friend")
    suspend fun addFriend(requesterId: String): AcceptFriendResponse
}
