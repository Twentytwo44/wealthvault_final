package com.wealthvault.`user-api`.updateuser

import com.wealthvault.`user-api`.model.UpdateUserDataResponse

interface UpdateUserApi {
    suspend fun updateUser(
        username: String,
        firstName: String,
        lastName: String,
        birthday: String,
        phoneNumber: String,
        profileImage: ByteArray?,
        sharedEnabled: Boolean? = null,
        sharedAge: Int? = null
    ): UpdateUserDataResponse
}