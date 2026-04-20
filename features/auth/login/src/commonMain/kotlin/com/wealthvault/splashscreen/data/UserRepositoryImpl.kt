package com.wealthvault.splashscreen.data

import com.wealthvault.`user-api`.model.UserData

class UserRepositoryImpl(
    private val networkDataSource: UserDataSource,
) {
    suspend fun getUser(): Result<UserData> {
        return networkDataSource.getUser()
    }
}
