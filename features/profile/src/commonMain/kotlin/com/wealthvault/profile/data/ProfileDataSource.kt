package com.wealthvault.profile.data

import com.wealthvault.`user-api`.model.UserData
import com.wealthvault.`user-api`.user.UserApi

class ProfileDataSource(
    private val userApi: UserApi,
) {
    suspend fun getUser(): Result<UserData> {
        return runCatching {
            val result = userApi.getUser()
            result.data ?: throw IllegalArgumentException("User is null, Cannot create user")
        }
    }
}

