package com.wealthvault.`user-api`.friend

import com.wealthvault.`user-api`.model.FriendResponse
import de.jensklingenberg.ktorfit.http.GET

interface FriendApi {
    @GET("friend")
    suspend fun getFriend(): FriendResponse
}
