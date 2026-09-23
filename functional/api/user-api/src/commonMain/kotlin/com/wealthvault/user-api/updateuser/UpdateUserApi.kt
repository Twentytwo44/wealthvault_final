package com.wealthvault.`user-api`.updateuser

import com.wealthvault.domain.profile.UpdateUserData
import com.wealthvault.domain.profile.UpdateUserDataRequest

interface UpdateUserApi {
    suspend fun updateUser(
       request: UpdateUserDataRequest
    ): UpdateUserData
}
