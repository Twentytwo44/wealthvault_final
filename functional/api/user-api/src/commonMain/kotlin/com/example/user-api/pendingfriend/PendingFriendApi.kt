package com.example.`user-api`.pendingfriend

import com.example.`user-api`.model.PendingFriendResponse
import de.jensklingenberg.ktorfit.http.GET

interface PendingFriendApi {
    @GET("friend/pending")
    suspend fun pendingfriend(): PendingFriendResponse
}
