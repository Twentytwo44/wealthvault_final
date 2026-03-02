package com.example.`user-api`.addfriend

import com.example.`user-api`.model.AcceptFriendRequest
import com.example.`user-api`.model.AcceptFriendResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST

interface AddFriendApi {
    @POST("friend")
    suspend fun addFriend(@Body request: AcceptFriendRequest): AcceptFriendResponse
}
