package com.wealthvault.`user-api`.user

import com.wealthvault.`user-api`.model.UserDataResponse
import de.jensklingenberg.ktorfit.http.GET

interface UserApi {
    @GET("user")
    suspend fun getUser(): UserDataResponse
}