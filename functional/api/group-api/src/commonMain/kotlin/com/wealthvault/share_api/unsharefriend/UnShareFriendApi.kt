package com.wealthvault.share_api.unsharefriend

import com.wealthvault.share_api.model.ShareItemResponse
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.Path

interface UnShareFriendApi {
    @DELETE("friend/item/{id}/")
    suspend fun unShareFriend(@Path("id") id: String): ShareItemResponse
}
