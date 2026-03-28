package com.wealthvault.profile.data

import com.wealthvault.`user-api`.model.UserData
import com.wealthvault.`user-api`.user.UserApi
import com.wealthvault.data_store.TokenStore
class ProfileDataSource(
    private val userApi: UserApi,
    private val tokenStore: TokenStore
) {
    suspend fun getUser(): Result<UserData> {
        return runCatching {

            val result = userApi.getUser("Bearer $tokenStore.accessToken")

            result.data ?: throw IllegalArgumentException("User is null, Cannot create user")
        }
    }
}

