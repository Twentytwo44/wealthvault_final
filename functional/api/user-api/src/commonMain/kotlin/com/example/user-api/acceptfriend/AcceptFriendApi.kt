package com.example.`user-api`.acceptfriend

import com.example.`user-api`.model.AcceptFriendRequest
import com.example.`user-api`.model.AcceptFriendResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST

interface AcceptFriendApi {
    @POST("friend/accept")
    suspend fun acceptFriend(@Body request: AcceptFriendRequest): AcceptFriendResponse
}
