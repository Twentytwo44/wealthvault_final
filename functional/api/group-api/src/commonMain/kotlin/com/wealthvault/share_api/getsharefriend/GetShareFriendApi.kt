package com.wealthvault.share_api.getsharefriend

import com.wealthvault.domain.social.ShareFriend

interface GetShareFriendApi {
    suspend fun getShareFriend(id: String): List<ShareFriend>

}
