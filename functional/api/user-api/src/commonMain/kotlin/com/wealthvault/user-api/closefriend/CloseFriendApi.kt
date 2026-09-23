package com.wealthvault.`user-api`.closefriend


import com.wealthvault.domain.profile.CloseFriendData

interface CloseFriendApi {
    suspend fun getCloseFriend(): List<CloseFriendData>
}
