package com.wealthvault.profile.data

import com.wealthvault.`user-api`.closefriend.CloseFriendApi // 🌟 1. Import API
import com.wealthvault.`user-api`.friend.FriendApi
import com.wealthvault.`user-api`.model.CloseFriendData    // 🌟 2. Import Model
import com.wealthvault.`user-api`.model.FriendData
import com.wealthvault.`user-api`.model.UserData
import com.wealthvault.`user-api`.user.UserApi
import com.wealthvault.`user-api`.model.UpdateUserData
import com.wealthvault.`user-api`.model.UpdateUserDataRequest
import com.wealthvault.`user-api`.updateclosefriend.UpdateCloseFriendApi
import com.wealthvault.`user-api`.updateuser.UpdateUserApi

class ProfileDataSource(
    private val userApi: UserApi,
    private val updateUserApi: UpdateUserApi,
    private val closeFriendApi: CloseFriendApi,
    private val updateCloseFriendApi: UpdateCloseFriendApi,
    private val friendApi: FriendApi
) {
    suspend fun getUser(): Result<UserData> {
        return runCatching {
            val result = userApi.getUser()
            result.data ?: throw IllegalArgumentException("User is null, Cannot create user")
        }
    }

    // 🌟 4. เพิ่มฟังก์ชันดึงคนสนิท
    suspend fun getCloseFriends(): Result<List<CloseFriendData>> {
        return runCatching {
            val result = closeFriendApi.getCloseFriend()
            result.data ?: emptyList() // ถ้าไม่มีให้ส่ง List เปล่ากลับไป
        }
    }

    suspend fun updateUserData(request: UpdateUserDataRequest): Result<UpdateUserData> {
        return runCatching {
            val result = updateUserApi.updateUser(
                username = request.username,
                firstName = request.firstName,
                lastName = request.lastName,
                birthday = request.birthday,
                phoneNumber = request.phoneNumber,
                profileImage = request.profileImage,
                sharedEnabled = request.sharedEnabled,
                sharedAge = request.sharedAge
            )
            result.data ?: throw IllegalArgumentException(result.error ?: "Update Failed")
        }
    }
    suspend fun updateCloseFriendStatus(friendId: String, isClose: Boolean): Result<Boolean> {
        return runCatching {
            val result = updateCloseFriendApi.updateCloseFriend(friendId, isClose)
            result.data?.success ?: throw IllegalArgumentException(result.status ?: "Update Friend Failed")
        }
    }

    suspend fun getAllFriends(): Result<List<FriendData>> {
        return runCatching {
            val result = friendApi.getFriend()
            result.data?.status ?: emptyList() // ดึงจากก้อน data.friend (ตาม Model FriendArray)
        }
    }
}

