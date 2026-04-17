package com.wealthvault.`user-api`.getuserbyemail

import com.wealthvault.`user-api`.model.SearchUserResponse

interface GetUserByEmailApi {
    // ไม่ต้องใช้ @POST หรือ @Body เพราะเราจะ Custom Request แบบ Form-data ใน Impl
    suspend fun searchUserByEmail(email: String): SearchUserResponse
}