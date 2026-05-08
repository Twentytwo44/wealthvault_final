package com.wealthvault.financiallist.data.share

import com.wealthvault.share_api.unsharefriend.UnShareFriendApi
import com.wealthvault.share_api.unsharegroup.UnShareGroupApi

class UnshareNetworkDataSource(
    private val unShareFriendApi: UnShareFriendApi,
    private val unShareGroupApi: UnShareGroupApi
) {
    suspend fun unshareFriend(id: String) = runCatching { unShareFriendApi.unShareFriend(id) }
    suspend fun unshareGroup(id: String) = runCatching { unShareGroupApi.unShareGroup(id) }
}