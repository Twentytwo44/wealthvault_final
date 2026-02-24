package com.example.`user-api`.friend

import com.example.`user-api`.model.FriendResponse
import de.jensklingenberg.ktorfit.http.GET

interface FriendApi {
    @GET("friend")
    suspend fun getFriend(): FriendResponse
}
