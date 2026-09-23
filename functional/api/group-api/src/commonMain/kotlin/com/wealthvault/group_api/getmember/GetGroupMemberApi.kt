package com.wealthvault.group_api.getmember

import com.wealthvault.domain.social.GroupMember

interface GetGroupMemberApi {
    suspend fun getGroupMembers(id: String): List<GroupMember>
}
