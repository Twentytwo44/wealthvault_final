package com.wealthvault.financiallist.data.share

import com.wealthvault.share_api.getsharefriend.GetShareFriendApi
import com.wealthvault.share_api.getsharegroup.GetShareGroupApi

// สร้างคลาสนี้เพื่อให้ ScreenModel เรียกใช้ได้
class UnshareRepositoryImpl(
    private val unShareFriendApi: com.wealthvault.share_api.unsharefriend.UnShareFriendApi,
    private val unShareGroupApi: com.wealthvault.share_api.unsharegroup.UnShareGroupApi,
    private val getShareFriendApi: GetShareFriendApi,
    private val getShareGroupApi: GetShareGroupApi
) {
    suspend fun unshareFriend(id: String) = runCatching { unShareFriendApi.unShareFriend(id) }
    suspend fun unshareGroup(id: String) = runCatching { unShareGroupApi.unShareGroup(id) }
    suspend fun getFriendItems(friendId: String) = runCatching { getShareFriendApi.getShareFriend(friendId) }
    suspend fun getGroupItems(groupId: String) = runCatching { getShareGroupApi.getShareGroup(groupId) }
}