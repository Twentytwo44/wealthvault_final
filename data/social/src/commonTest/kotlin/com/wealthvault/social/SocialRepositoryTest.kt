package com.wealthvault.social

import com.wealthvault.core.architecture.CacheFreshness
import com.wealthvault.core.architecture.AppError
import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.cache.FeatureCache
import com.wealthvault.core.cache.FeatureCacheEntry
import com.wealthvault.core.observability.NoOpAppLogger
import com.wealthvault.domain.portfolio.BankAccountData
import com.wealthvault.domain.portfolio.BuildingIdData
import com.wealthvault.domain.portfolio.CashIdData
import com.wealthvault.domain.portfolio.InsuranceIdData
import com.wealthvault.domain.portfolio.InvestmentIdData
import com.wealthvault.domain.portfolio.LandIdData
import com.wealthvault.domain.portfolio.LiabilityIdData
import com.wealthvault.domain.profile.FriendData
import com.wealthvault.domain.social.AcceptFriendRequest
import com.wealthvault.domain.social.AcceptFriendResult
import com.wealthvault.domain.social.FriendProfile
import com.wealthvault.domain.social.GroupData
import com.wealthvault.domain.social.GroupMember
import com.wealthvault.domain.social.GroupMessage
import com.wealthvault.domain.social.GroupResult
import com.wealthvault.domain.social.GroupSummary
import com.wealthvault.domain.social.MessageItem
import com.wealthvault.domain.social.PendingFriend
import com.wealthvault.domain.social.ShareFriend
import com.wealthvault.domain.social.ShareGroup
import com.wealthvault.domain.social.ShareItems
import com.wealthvault.domain.social.ShareableItem
import com.wealthvault.data.social.repository.SocialRemoteDataSource
import com.wealthvault.data.social.repository.SocialRepositoryImpl
import com.wealthvault.data.social.repository.toCacheRecord
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class SocialRepositoryTest {
    @Test
    fun freshFriendsCacheAvoidsDuplicateNetworkRequest() = runTest {
        val remote = FakeRemote()
        val cache = MemoryCache()
        val repository = SocialRepositoryImpl(remote, NoOpAppLogger, cache, Json)

        assertEquals(listOf(FRIEND), repository.getAllFriends().getOrThrow())
        assertEquals(listOf(FRIEND), repository.getAllFriends().getOrThrow())
        assertEquals(1, remote.friendsCalls)
    }

    @Test
    fun staleFriendsCacheIsReturnedOfflineAndPublishesFreshness() = runTest {
        val remote = FakeRemote().apply { failFriends = true }
        val cache = MemoryCache().apply {
            entries["social" to "friends"] = FeatureCacheEntry(
                payload = Json.encodeToString(listOf(FRIEND.toCacheRecord())),
                updatedAtEpochMillis = 0,
            )
        }
        val repository = SocialRepositoryImpl(remote, NoOpAppLogger, cache, Json)

        assertEquals(listOf(FRIEND), repository.getAllFriends().getOrThrow())
        assertEquals(1, remote.friendsCalls)
        assertEquals(CacheFreshness.Offline, repository.observeAllFriends().first().freshness)
    }

    @Test
    fun staleFriendsRemainVisibleWhileRefreshIsInFlight() = runTest {
        val refreshGate = CompletableDeferred<Unit>()
        val remote = FakeRemote().apply { friendsGate = refreshGate }
        val cache = MemoryCache().apply {
            entries["social" to "friends"] = FeatureCacheEntry(
                payload = Json.encodeToString(listOf(FRIEND.toCacheRecord())),
                updatedAtEpochMillis = 0,
            )
        }
        val repository = SocialRepositoryImpl(remote, NoOpAppLogger, cache, Json)
        val stale = async { repository.observeAllFriends().first() }
        val refresh = launch { repository.getAllFriends(force = true) }

        runCurrent()
        assertEquals(CacheFreshness.Stale, stale.await().freshness)

        refreshGate.complete(Unit)
        refresh.join()
        assertEquals(CacheFreshness.Fresh, repository.observeAllFriends().first().freshness)
    }

    @Test
    fun delegatesSocialReadsAndMutationsThroughOneRepositoryContract() = runTest {
        val remote = FakeRemote()
        val cache = MemoryCache()
        val repository = SocialRepositoryImpl(remote, NoOpAppLogger, cache, Json)

        assertEquals(listOf(GROUP), repository.getAllGroups().getOrThrow())
        assertEquals(GROUP_RESULT, repository.createGroup("group", listOf("member"), byteArrayOf(1)).getOrThrow())
        assertEquals(FRIEND, repository.searchUser("friend@example.com").getOrThrow())
        assertEquals(true, repository.addFriend("friend-1").getOrThrow())
        assertEquals(listOf(MESSAGE), repository.getFriendMessages("friend-1").getOrThrow())
        assertEquals(PROFILE, repository.getFriendProfile("friend-1").getOrThrow())
        assertEquals(BANK_ACCOUNT, repository.getAccountById("account-1").getOrThrow())
        assertEquals(BUILDING, repository.getBuildingById("building-1").getOrThrow())
        assertEquals(CASH, repository.getCashById("cash-1").getOrThrow())
        assertEquals(INSURANCE, repository.getInsuranceById("insurance-1").getOrThrow())
        assertEquals(INVESTMENT, repository.getInvestmentById("investment-1").getOrThrow())
        assertEquals(LAND, repository.getLandById("land-1").getOrThrow())
        assertEquals(LIABILITY, repository.getLiabilityById("liability-1").getOrThrow())
        assertEquals(listOf(GROUP_MESSAGE), repository.getGroupMessages("group-1").getOrThrow())
        assertEquals(listOf(SHARE_FRIEND), repository.getShareFriendItems("friend-1").getOrThrow())
        assertEquals(listOf(SHARE_GROUP), repository.getShareGroupItems("group-1").getOrThrow())
        assertEquals(true, repository.grantAccess("group-1", "friend-1", listOf("asset-1")).getOrThrow())
        assertEquals(GROUP_DATA, repository.getGroupDetail("group-1").getOrThrow())
        assertEquals(listOf(GROUP_MEMBER), repository.getGroupMembers("group-1").getOrThrow())
        assertEquals(GROUP_RESULT, repository.updateGroup("group-1", "new name", byteArrayOf(2)).getOrThrow())
        assertEquals(true, repository.addGroupMember("group-1", "friend-1").getOrThrow())
        assertEquals(true, repository.removeGroupMember("group-1", "friend-1").getOrThrow())
        assertEquals(true, repository.leaveGroup("group-1").getOrThrow())
        assertEquals(true, repository.removeFriend("friend-1").getOrThrow())
        assertEquals(listOf(SHAREABLE), repository.getItemsToShare("friend-1", false).getOrThrow())
        assertEquals(true, repository.submitShareItems(ShareItems()).getOrThrow())
        assertEquals(true, repository.unShareAsset("shared-1", false).getOrThrow())
        assertEquals(true, repository.unShareAsset("shared-2", true).getOrThrow())
        assertEquals(true, repository.deleteGroup("group-1").getOrThrow())
        assertEquals(ACCEPT_RESULT, repository.acceptFriend(AcceptFriendRequest("friend-1", "accept")).getOrThrow())
        assertEquals(listOf(PENDING), repository.getPendingFriends().getOrThrow())
    }

    @Test
    fun refreshLoadsAllSocialReadModels() = runTest {
        val repository = SocialRepositoryImpl(FakeRemote(), NoOpAppLogger, MemoryCache(), Json)

        assertEquals(Unit, repository.refresh(force = true).getOrThrow())
        assertEquals(listOf(FRIEND), repository.observeAllFriends().first().value)
        assertEquals(listOf(GROUP), repository.observeAllGroups().first().value)
        assertEquals(listOf(PENDING), repository.observePendingFriends().first().value)
    }

    @Test
    fun missingSharedAssetIsReportedAsNotFoundInsteadOfThrowing() = runTest {
        val remote = FakeRemote().apply { missingAccount = true }
        val repository = SocialRepositoryImpl(remote, NoOpAppLogger, MemoryCache(), Json)

        val result = repository.getAccountById("deleted-account")

        assertEquals(AppResult.Failure(AppError.NotFound), result)
    }

    private class MemoryCache : FeatureCache {
        val entries = mutableMapOf<Pair<String, String>, FeatureCacheEntry>()

        override suspend fun read(namespace: String, key: String): FeatureCacheEntry? = entries[namespace to key]

        override suspend fun write(namespace: String, key: String, payload: String, updatedAtEpochMillis: Long) {
            entries[namespace to key] = FeatureCacheEntry(payload, updatedAtEpochMillis)
        }

        override suspend fun clear(namespace: String, key: String) {
            entries.remove(namespace to key)
        }

        override suspend fun clearNamespace(namespace: String) {
            entries.keys.removeAll { it.first == namespace }
        }
    }

    private class FakeRemote : SocialRemoteDataSource {
        var friendsCalls = 0
        var failFriends = false
        var missingAccount = false
        var friendsGate: CompletableDeferred<Unit>? = null

        override suspend fun getAllFriends(): AppResult<List<FriendData>> {
            friendsCalls += 1
            friendsGate?.await()
            return if (failFriends) {
                AppResult.Failure(AppError.Unknown(IllegalStateException("offline")))
            } else {
                AppResult.Success(listOf(FRIEND))
            }
        }

        override suspend fun getAllGroups() = AppResult.Success(listOf(GROUP))
        override suspend fun createGroup(groupName: String, memberIds: List<String>, imageBytes: ByteArray?) = AppResult.Success(GROUP_RESULT)
        override suspend fun searchUser(email: String) = AppResult.Success(FRIEND)
        override suspend fun addFriend(targetId: String) = AppResult.Success(true)
        override suspend fun getFriendMessages(friendId: String) = AppResult.Success(listOf(MESSAGE))
        override suspend fun getFriendProfile(friendId: String) = AppResult.Success(PROFILE)
        override suspend fun getAccountById(id: String) =
            if (missingAccount) AppResult.Success(null) else AppResult.Success(BANK_ACCOUNT)
        override suspend fun getBuildingById(id: String) = AppResult.Success(BUILDING)
        override suspend fun getCashById(id: String) = AppResult.Success(CASH)
        override suspend fun getInsuranceById(id: String) = AppResult.Success(INSURANCE)
        override suspend fun getInvestmentById(id: String) = AppResult.Success(INVESTMENT)
        override suspend fun getLandById(id: String) = AppResult.Success(LAND)
        override suspend fun getLiabilityById(id: String) = AppResult.Success(LIABILITY)
        override suspend fun getGroupMessages(groupId: String) = AppResult.Success(listOf(GROUP_MESSAGE))
        override suspend fun getShareFriendItems(friendId: String) = AppResult.Success(listOf(SHARE_FRIEND))
        override suspend fun getShareGroupItems(groupId: String) = AppResult.Success(listOf(SHARE_GROUP))
        override suspend fun grantAccess(groupId: String, targetId: String, itemIds: List<String>) = AppResult.Success(true)
        override suspend fun getGroupDetail(groupId: String) = AppResult.Success(GROUP_DATA)
        override suspend fun getGroupMembers(groupId: String) = AppResult.Success(listOf(GROUP_MEMBER))
        override suspend fun updateGroup(groupId: String, groupName: String, profileImage: ByteArray?) = AppResult.Success(GROUP_RESULT)
        override suspend fun addGroupMember(groupId: String, targetId: String) = AppResult.Success(true)
        override suspend fun removeGroupMember(groupId: String, targetId: String) = AppResult.Success(true)
        override suspend fun leaveGroup(groupId: String) = AppResult.Success(true)
        override suspend fun removeFriend(targetId: String) = AppResult.Success(true)
        override suspend fun getItemsToShare(targetId: String, isGroup: Boolean) = AppResult.Success(listOf(SHAREABLE))
        override suspend fun submitShareItems(request: ShareItems) = AppResult.Success(true)
        override suspend fun unShareFriendItem(id: String) = AppResult.Success(true)
        override suspend fun unShareGroupItem(id: String) = AppResult.Success(true)
        override suspend fun deleteGroup(groupId: String) = AppResult.Success(true)
        override suspend fun acceptFriend(request: AcceptFriendRequest) = AppResult.Success(ACCEPT_RESULT)
        override suspend fun getPendingFriends() = AppResult.Success(listOf(PENDING))

        private fun <T> unsupported(): AppResult<T> =
            AppResult.Failure(AppError.Unknown(UnsupportedOperationException()))
    }

    private companion object {
        val FRIEND = FriendData(id = "friend-1", email = "friend@example.com")
        val GROUP = GroupSummary(id = "group-1", groupName = "Group")
        val GROUP_DATA = GroupData(id = "group-1", groupName = "Group", memberCount = 1)
        val GROUP_RESULT = GroupResult(status = "success", data = GROUP_DATA)
        val GROUP_MEMBER = GroupMember(id = "member-1", username = "member")
        val MESSAGE = MessageItem(id = "message-1", content = "hello")
        val GROUP_MESSAGE = GroupMessage(senderId = "member-1", content = "hello")
        val PROFILE = FriendProfile()
        val SHARE_FRIEND = ShareFriend(groupItemId = "shared-friend")
        val SHARE_GROUP = ShareGroup(groupItemId = "shared-group")
        val SHAREABLE = ShareableItem(id = "asset-1", name = "Asset")
        val PENDING = PendingFriend(id = "pending-1")
        val ACCEPT_RESULT = AcceptFriendResult(success = "accepted")
        val BANK_ACCOUNT = BankAccountData("account-1", "user-1")
        val BUILDING = BuildingIdData(id = "building-1", userId = "user-1")
        val CASH = CashIdData("cash-1", "user-1")
        val INSURANCE = InsuranceIdData("insurance-1", "user-1")
        val INVESTMENT = InvestmentIdData("investment-1", "user-1")
        val LAND = LandIdData("land-1", "user-1")
        val LIABILITY = LiabilityIdData("liability-1", "user-1")
    }
}
