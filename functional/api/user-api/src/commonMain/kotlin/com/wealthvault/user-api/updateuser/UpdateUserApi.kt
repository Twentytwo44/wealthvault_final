package com.wealthvault.`user-api`.updateuser

import com.wealthvault.`user-api`.model.UpdateUserDataResponse
import de.jensklingenberg.ktorfit.http.Multipart // 🌟 เพิ่ม import
import de.jensklingenberg.ktorfit.http.PATCH
import de.jensklingenberg.ktorfit.http.Part // 🌟 เพิ่ม import

interface UpdateUserApi {
    suspend fun updateUser(
        username:String,
        firstName: String,
        lastName: String,
        birthday: String,
        phoneNumber: String,
        profileImage: ByteArray?
    ): UpdateUserDataResponse
}