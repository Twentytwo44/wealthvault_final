package com.wealthvault.`user-api`.deletefriend

import com.wealthvault.`user-api`.model.DeleteFriendResponse
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.Path

interface DeleteFriendApi {
    @DELETE("friend/{id}")
    suspend fun deleteFriend(@Path("id") id: String): DeleteFriendResponse
}