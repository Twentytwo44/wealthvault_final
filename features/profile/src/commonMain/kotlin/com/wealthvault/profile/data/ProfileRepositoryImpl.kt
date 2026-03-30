package com.wealthvault.profile.data

import com.wealthvault.`user-api`.model.CloseFriendData // 🌟 Import Model
import com.wealthvault.`user-api`.model.UserData
import com.wealthvault.`user-api`.model.UpdateUserData
import com.wealthvault.`user-api`.model.UpdateUserDataRequest

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

    // 🌟 เพิ่มฟังก์ชันดึงคนสนิท
    suspend fun getCloseFriends(): Result<List<CloseFriendData>> {
        return networkDataSource.getCloseFriends().onSuccess { friends ->
            println("✅ Fetched Close Friends: ${friends.size} persons")
        }.onFailure { error ->
            println("🚨 Get Close Friends Failed: ${error.message}")
        }
    }

    suspend fun updateUserData(request: UpdateUserDataRequest): Result<UpdateUserData> {
        // สั่งให้ DataSource (พนักงานส่งเอกสาร) เอาข้อมูลไปยิง API
        return networkDataSource.updateUserData(request).onSuccess {
            println("✅ Update User Success!")
        }.onFailure { error ->
            println("🚨 Update User Failed: ${error.message}")
        }
    }
}
