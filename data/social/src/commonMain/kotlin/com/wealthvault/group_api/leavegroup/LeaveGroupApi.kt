package com.wealthvault.data.social.group.transport.leavegroup

interface LeaveGroupApi {
    suspend fun leaveGroup(id: String): Boolean
}
