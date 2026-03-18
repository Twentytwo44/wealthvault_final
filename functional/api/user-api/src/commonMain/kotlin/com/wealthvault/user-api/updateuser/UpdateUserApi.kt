package com.wealthvault.`user-api`.updateuser

import com.wealthvault.`user-api`.model.UpdateUserDataRequest
import com.wealthvault.`user-api`.model.UpdateUserDataResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.PATCH

interface UpdateUserApi {
    @PATCH("user")
    suspend fun updateUser(@Body request: UpdateUserDataRequest): UpdateUserDataResponse
}
