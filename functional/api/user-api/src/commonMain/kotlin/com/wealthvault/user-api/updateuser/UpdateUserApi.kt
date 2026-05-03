package com.wealthvault.`user-api`.updateuser

import com.wealthvault.`user-api`.model.UpdateUserDataRequest
import com.wealthvault.`user-api`.model.UpdateUserDataResponse

interface UpdateUserApi {
    suspend fun updateUser(
       request: UpdateUserDataRequest
    ): UpdateUserDataResponse
}
