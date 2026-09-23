package com.wealthvault.`user-api`.user

import com.wealthvault.domain.profile.UserData

interface UserApi {
    suspend fun getUser(): UserData
}
