package com.wealthvault.domain.social

import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.architecture.CachedValue
import com.wealthvault.core.model.BankAccountData
import com.wealthvault.core.model.BuildingIdData
import com.wealthvault.core.model.CashIdData
import com.wealthvault.core.model.FriendData
import com.wealthvault.core.model.InsuranceIdData
import com.wealthvault.core.model.InvestmentIdData
import com.wealthvault.core.model.LandIdData
import com.wealthvault.core.model.LiabilityIdData
import kotlinx.coroutines.flow.Flow

/**
 * Transport-neutral group chat boundary.  The social presentation layer owns
 * the chat lifecycle through this contract; Ktor/WebSocket details stay in
 * the data adapter.
 */
sealed interface GroupChatEvent {
    data class Message(val value: GroupMessage) : GroupChatEvent

    data class DataUpdated(val groupId: String) : GroupChatEvent
}

interface GroupChatGateway {
    /** Opens the configured authenticated chat endpoint without exposing URL configuration to UI. */
    suspend fun connectToChat(accessToken: String?): Flow<GroupChatEvent>

    suspend fun connect(url: String): Flow<GroupChatEvent>

    /**
     * Authenticated connection variant.  The token is passed out-of-band so
     * it never becomes part of a URL, proxy log, or analytics breadcrumb.
     * The default keeps older adapters source-compatible during migration.
     */
    suspend fun connect(url: String, accessToken: String?): Flow<GroupChatEvent> = connect(url)

    suspend fun send(action: GroupChatAction)
    suspend fun close()
}

/**
 * Presentation-facing social contract.
 *
 * This is the single result contract crossing the social domain boundary.
 * Implementations own transport mapping and persistence; feature code depends
 * on this contract rather than a concrete data class.
 */
interface SocialRepository {
    /** Refreshes all social read models; force bypasses the bounded-context TTL. */
    suspend fun refresh(force: Boolean = false): AppResult<Unit>

    suspend fun getAllFriends(force: Boolean = false): AppResult<List<FriendData>>
    suspend fun getAllGroups(force: Boolean = false): AppResult<List<GroupSummary>>
    suspend fun createGroup(
        groupName: String,
        memberIds: List<String>,
        imageBytes: ByteArray?,
    ): AppResult<GroupResult>

    suspend fun searchUser(email: String): AppResult<FriendData?>
    suspend fun addFriend(targetId: String): AppResult<Boolean>
    suspend fun getFriendMessages(friendId: String): AppResult<List<MessageItem>>
    suspend fun getFriendProfile(friendId: String): AppResult<FriendProfile>

    suspend fun getAccountById(id: String): AppResult<BankAccountData>
    suspend fun getBuildingById(id: String): AppResult<BuildingIdData>
    suspend fun getCashById(id: String): AppResult<CashIdData>
    suspend fun getInsuranceById(id: String): AppResult<InsuranceIdData>
    suspend fun getInvestmentById(id: String): AppResult<InvestmentIdData>
    suspend fun getLandById(id: String): AppResult<LandIdData>
    suspend fun getLiabilityById(id: String): AppResult<LiabilityIdData>

    suspend fun getGroupMessages(groupId: String): AppResult<List<GroupMessage>>
    suspend fun getShareFriendItems(friendId: String): AppResult<List<ShareFriend>>
    suspend fun getShareGroupItems(groupId: String): AppResult<List<ShareGroup>>
    suspend fun grantAccess(groupId: String, targetId: String, itemIds: List<String>): AppResult<Boolean>
    suspend fun getGroupDetail(groupId: String): AppResult<GroupData>
    suspend fun getGroupMembers(groupId: String): AppResult<List<GroupMember>>
    suspend fun updateGroup(
        groupId: String,
        groupName: String,
        profileImage: ByteArray? = null,
    ): AppResult<GroupResult>

    suspend fun addGroupMember(groupId: String, targetId: String): AppResult<Boolean>
    suspend fun removeGroupMember(groupId: String, targetId: String): AppResult<Boolean>
    suspend fun leaveGroup(groupId: String): AppResult<Boolean>
    suspend fun removeFriend(targetId: String): AppResult<Boolean>
    suspend fun getItemsToShare(targetId: String, isGroup: Boolean): AppResult<List<ShareableItem>>
    suspend fun submitShareItems(request: ShareItems): AppResult<Boolean>
    suspend fun unShareAsset(sharedItemId: String, isGroup: Boolean): AppResult<Boolean>
    suspend fun deleteGroup(groupId: String): AppResult<Boolean>
    suspend fun acceptFriend(request: AcceptFriendRequest): AppResult<AcceptFriendResult>
    suspend fun getPendingFriends(force: Boolean = false): AppResult<List<PendingFriend>>

    fun observeAllFriends(): Flow<CachedValue<List<FriendData>>>
    fun observeAllGroups(): Flow<CachedValue<List<GroupSummary>>>
    fun observePendingFriends(): Flow<CachedValue<List<PendingFriend>>>
}

/** Compatibility group lookup contract used by the financial forms. */
interface GroupDirectoryRepository {
    suspend fun getAllGroup(): AppResult<List<GroupSummary>>
}

/** Compatibility share mutation contract used by the financial forms. */
interface ShareItemRepository {
    suspend fun shareItem(request: ShareItems): AppResult<String>
}

interface ShareTargetsRepository {
    suspend fun shareTargets(id: String, type: String): AppResult<ShareTargets>
}

/** Unshare and shared-item lookup boundary for the portfolio forms. */
interface UnshareRepository {
    suspend fun unshareFriend(id: String): AppResult<Unit>
    suspend fun unshareGroup(id: String): AppResult<Unit>
    suspend fun getFriendItems(friendId: String): AppResult<List<ShareFriend>>
    suspend fun getGroupItems(groupId: String): AppResult<List<ShareGroup>>
}
