package com.wealthvault.data.social.repository

import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.model.BankAccountData
import com.wealthvault.core.model.BuildingIdData
import com.wealthvault.core.model.CashIdData
import com.wealthvault.core.model.FriendData
import com.wealthvault.core.model.InsuranceIdData
import com.wealthvault.core.model.InvestmentIdData
import com.wealthvault.core.model.LandIdData
import com.wealthvault.core.model.LiabilityIdData
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

/** Transport-independent data source contract used by the social repository. */
internal interface SocialRemoteDataSource {
    suspend fun getAllFriends(): AppResult<List<FriendData>>
    suspend fun getAllGroups(): AppResult<List<GroupSummary>>
    suspend fun createGroup(
        groupName: String,
        memberIds: List<String>,
        imageBytes: ByteArray?,
    ): AppResult<GroupResult>

    suspend fun searchUser(email: String): AppResult<FriendData?>
    suspend fun addFriend(targetId: String): AppResult<Boolean>
    suspend fun getFriendMessages(friendId: String): AppResult<List<MessageItem>>
    suspend fun getFriendProfile(friendId: String): AppResult<FriendProfile>

    suspend fun getAccountById(id: String): AppResult<BankAccountData?>
    suspend fun getBuildingById(id: String): AppResult<BuildingIdData?>
    suspend fun getCashById(id: String): AppResult<CashIdData?>
    suspend fun getInsuranceById(id: String): AppResult<InsuranceIdData?>
    suspend fun getInvestmentById(id: String): AppResult<InvestmentIdData?>
    suspend fun getLandById(id: String): AppResult<LandIdData?>
    suspend fun getLiabilityById(id: String): AppResult<LiabilityIdData?>

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
    suspend fun unShareFriendItem(id: String): AppResult<Boolean>
    suspend fun unShareGroupItem(id: String): AppResult<Boolean>
    suspend fun deleteGroup(groupId: String): AppResult<Boolean>
    suspend fun acceptFriend(request: AcceptFriendRequest): AppResult<AcceptFriendResult>
    suspend fun getPendingFriends(): AppResult<List<PendingFriend>>
}
