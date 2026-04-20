package com.wealthvault.splashscreen.data

import com.wealthvault.`user-api`.model.UserData
import com.wealthvault.`user-api`.user.UserApi


class UserDataSource(
    private val userApi: UserApi,
) {
    // เปลี่ยนจาก GetGroupResponse เป็น List<GetAllGroupData>
    suspend fun getUser(): Result<UserData> {
        return runCatching {
            val result = userApi.getUser()
            println("result: $result")
            // ส่ง List กลับไป ถ้า null ก็โยน Error
            result.data ?: throw IllegalArgumentException(result.error)
        }
    }
}
