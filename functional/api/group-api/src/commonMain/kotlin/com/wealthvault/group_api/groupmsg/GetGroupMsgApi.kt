package com.wealthvault.group_api.groupmsg

import com.wealthvault.domain.social.GroupMessage

interface GetGroupMsgApi {
    suspend fun getGroupMsg(id: String): List<GroupMessage>
}
