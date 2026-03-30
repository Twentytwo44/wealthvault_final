package com.wealthvault.`user-api`.closefriend


import com.wealthvault.`user-api`.model.AcceptFriendRequest
import com.wealthvault.`user-api`.model.AcceptFriendResponse
import com.wealthvault.`user-api`.model.CloseFriendResponse
import com.wealthvault.`user-api`.model.FriendResponse
import com.wealthvault.`user-api`.model.UserDataResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET

interface CloseFriendApi {
    @GET("closefriend")
    suspend fun getCloseFriend(): CloseFriendResponse
}
