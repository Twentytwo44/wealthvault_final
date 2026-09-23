package com.wealthvault.group_api.leavegroup

interface LeaveGroupApi {
    suspend fun leaveGroup(id: String): Boolean
}
