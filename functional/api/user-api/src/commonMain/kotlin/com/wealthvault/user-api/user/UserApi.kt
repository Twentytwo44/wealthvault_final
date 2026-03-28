package com.wealthvault.`user-api`.user

import com.wealthvault.`user-api`.model.UserDataResponse
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Header

interface UserApi {
    @GET("user/") // 🌟 เติม / ปิดท้ายให้มันนิดนึงครับ
    suspend fun getUser(
        @Header("Authorization") token: String // 🌟 บังคับว่าตอนเรียกต้องส่ง Token มาด้วย!
    ): UserDataResponse
}
