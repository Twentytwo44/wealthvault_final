package com.example.`user-api`.updateuser

import com.example.`user-api`.model.UpdateUserDataRequest
import com.example.`user-api`.model.UpdateUserDataResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.PATCH

interface UpdateUserApi {
    @PATCH("user")
    suspend fun updateUser(@Body request: UpdateUserDataRequest): UpdateUserDataResponse
}
