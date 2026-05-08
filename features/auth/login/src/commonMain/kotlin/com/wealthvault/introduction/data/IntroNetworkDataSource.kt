package com.wealthvault.introduction.data

import com.wealthvault.`user-api`.model.UpdateUserData
import com.wealthvault.`user-api`.model.UpdateUserDataRequest
import com.wealthvault.`user-api`.updateuser.UpdateUserApi

class IntroNetworkDataSource(
    private val introApi: UpdateUserApi,
) {
    suspend fun updateUser(request: UpdateUserDataRequest): Result<UpdateUserData> {
        return runCatching {
            val result = introApi.updateUser(request)
            result.data ?: throw IllegalArgumentException("User is null, Cannot create user")
        }
    }
}
