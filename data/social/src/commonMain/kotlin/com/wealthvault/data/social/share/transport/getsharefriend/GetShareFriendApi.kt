package com.wealthvault.data.social.share.transport.getsharefriend

import com.wealthvault.domain.social.ShareFriend

interface GetShareFriendApi {
    suspend fun getShareFriend(id: String): List<ShareFriend>

}
