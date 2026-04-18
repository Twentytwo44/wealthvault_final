package com.wealthvault.share_api.getsharefriend

import com.wealthvault.share_api.model.ShareFriendResponse
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path

interface GetShareFriendApi {
    @GET("friend/{id}/item/")
    suspend fun getShareFriend(@Path("id") id: String): ShareFriendResponse

}
