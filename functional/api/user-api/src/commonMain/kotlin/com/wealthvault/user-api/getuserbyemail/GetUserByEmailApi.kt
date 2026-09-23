package com.wealthvault.`user-api`.getuserbyemail

import com.wealthvault.domain.profile.FriendData

interface GetUserByEmailApi {
    // ไม่ต้องใช้ @POST หรือ @Body เพราะเราจะ Custom Request แบบ Form-data ใน Impl
    suspend fun searchUserByEmail(email: String): List<FriendData>
}
