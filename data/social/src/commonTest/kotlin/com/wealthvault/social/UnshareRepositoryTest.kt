package com.wealthvault.social

import com.wealthvault.core.architecture.AppResult
import com.wealthvault.data.social.share.transport.getsharefriend.GetShareFriendApi
import com.wealthvault.data.social.share.transport.getsharegroup.GetShareGroupApi
import com.wealthvault.data.social.share.transport.unsharefriend.UnShareFriendApi
import com.wealthvault.data.social.share.transport.unsharegroup.UnShareGroupApi
import com.wealthvault.domain.social.ShareFriend
import com.wealthvault.domain.social.ShareGroup
import com.wealthvault.social.data.share.UnshareRepositoryImpl
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class UnshareRepositoryTest {
    @Test
    fun delegatesUnshareAndShareQueriesAndConvertsExceptions() = runTest {
        val apis = FakeApis()
        val repository = UnshareRepositoryImpl(apis, apis, apis, apis)

        assertIs<AppResult.Success<Unit>>(repository.unshareFriend("friend-item"))
        assertIs<AppResult.Success<Unit>>(repository.unshareGroup("group-item"))
        assertEquals(listOf(FRIEND_ITEM), repository.getFriendItems("friend-1").getOrThrow())
        assertEquals(listOf(GROUP_ITEM), repository.getGroupItems("group-1").getOrThrow())

        apis.fail = true
        assertIs<AppResult.Failure>(repository.unshareFriend("friend-item"))
        assertIs<AppResult.Failure>(repository.unshareGroup("group-item"))
        assertIs<AppResult.Failure>(repository.getFriendItems("friend-1"))
        assertIs<AppResult.Failure>(repository.getGroupItems("group-1"))
    }

    private class FakeApis : UnShareFriendApi, UnShareGroupApi, GetShareFriendApi, GetShareGroupApi {
        var fail = false

        override suspend fun unShareFriend(id: String) {
            if (fail) error("friend unshare failed")
        }

        override suspend fun unShareGroup(id: String) {
            if (fail) error("group unshare failed")
        }

        override suspend fun getShareFriend(id: String): List<ShareFriend> {
            if (fail) throw IllegalStateException("friend list failed")
            return listOf(FRIEND_ITEM)
        }

        override suspend fun getShareGroup(id: String): List<ShareGroup> {
            if (fail) throw IllegalStateException("group list failed")
            return listOf(GROUP_ITEM)
        }
    }

    private companion object {
        val FRIEND_ITEM = ShareFriend(groupItemId = "friend-item")
        val GROUP_ITEM = ShareGroup(groupItemId = "group-item")
    }
}
