package com.wealthvault.`user-api`.friendmsg

import com.wealthvault.domain.social.MessageItem

interface GetFriendMsgApi {
    suspend fun getFriendMsg(id: String): List<MessageItem>
}
