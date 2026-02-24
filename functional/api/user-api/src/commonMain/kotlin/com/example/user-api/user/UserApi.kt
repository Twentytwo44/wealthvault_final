package com.example.`user-api`.user

import com.example.`user-api`.model.UserDataResponse
import de.jensklingenberg.ktorfit.http.GET

interface UserApi {
    @GET("user")
    suspend fun getuser(): UserDataResponse
}
