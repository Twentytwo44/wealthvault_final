package com.wealthvault.data.social.repository.share

import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.architecture.runSuspendAppCatching
import com.wealthvault.data.social.share.transport.getsharefriend.GetShareFriendApi
import com.wealthvault.data.social.share.transport.getsharegroup.GetShareGroupApi
import com.wealthvault.data.social.share.transport.unsharefriend.UnShareFriendApi
import com.wealthvault.data.social.share.transport.unsharegroup.UnShareGroupApi
import com.wealthvault.domain.social.ShareFriend
import com.wealthvault.domain.social.ShareGroup
import com.wealthvault.domain.social.UnshareRepository

internal class UnshareRepositoryImpl(
    private val unShareFriendApi: UnShareFriendApi,
    private val unShareGroupApi: UnShareGroupApi,
    private val getShareFriendApi: GetShareFriendApi,
    private val getShareGroupApi: GetShareGroupApi,
) : UnshareRepository {
    override suspend fun unshareFriend(id: String): AppResult<Unit> = runSuspendAppCatching {
        unShareFriendApi.unShareFriend(id)
        Unit
    }

    override suspend fun unshareGroup(id: String): AppResult<Unit> = runSuspendAppCatching {
        unShareGroupApi.unShareGroup(id)
        Unit
    }

    override suspend fun getFriendItems(friendId: String): AppResult<List<ShareFriend>> =
        runSuspendAppCatching { getShareFriendApi.getShareFriend(friendId) }

    override suspend fun getGroupItems(groupId: String): AppResult<List<ShareGroup>> =
        runSuspendAppCatching { getShareGroupApi.getShareGroup(groupId) }
}
