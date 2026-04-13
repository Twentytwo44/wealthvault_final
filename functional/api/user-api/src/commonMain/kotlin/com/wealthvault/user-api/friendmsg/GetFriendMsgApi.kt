package com.wealthvault.`user-api`.friendmsg

import com.wealthvault.`user-api`.model.MessageResponse
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path

interface GetFriendMsgApi {
    @GET("friend/{id}/msg/")
    suspend fun getFriendMsg(@Path("id") id: String): MessageResponse
}
