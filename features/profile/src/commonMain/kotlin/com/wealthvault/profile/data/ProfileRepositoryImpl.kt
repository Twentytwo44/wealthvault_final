package com.wealthvault.profile.data

import com.wealthvault.`user-api`.model.UserData


class ProfileRepositoryImpl(
    private val networkDataSource: ProfileDataSource,
) {
    suspend fun getUser(): Result<UserData> {


        return networkDataSource.getUser().onSuccess { user ->
            println("✅ Fetched User: $user")
        }.onFailure { error ->
            println("🚨 Get User Failed: ${error.message}")
        }

    }
}
