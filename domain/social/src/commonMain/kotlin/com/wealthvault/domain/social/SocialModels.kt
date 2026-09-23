package com.wealthvault.domain.social

import com.wealthvault.core.model.Money

data class PendingFriend(
    val id: String? = null,
    val username: String? = null,
    val email: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val phoneNumber: String? = null,
    val profile: String? = null,
    val birthday: String? = null,
    val sharedAge: Int? = null,
    val sharedEnabled: Boolean? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val isFriend: Boolean? = null,
    val isClose: Boolean? = null,
)

data class FriendProfile(
    val userInfo: FriendUserInfo? = null,
    val itemPreview: List<ItemPreview> = emptyList(),
)

data class FriendUserInfo(
    val id: String? = null,
    val username: String? = null,
    val email: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val phoneNumber: String? = null,
    val profile: String? = null,
    val birthday: String? = null,
    val sharedAge: Int? = null,
    val sharedEnabled: Boolean? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val isFriend: Boolean? = null,
    val isClose: Boolean? = null,
)

data class ItemPreview(
    val itemId: String? = null,
    val type: String? = null,
    val assetDetail: AssetDetailPreview? = null,
)

data class AssetDetailPreview(
    val id: String? = null,
    val name: String? = null,
    val bankName: String? = null,
    val accountNumber: String? = null,
    val amount: Money? = null,
    val companyName: String? = null,
    val polNum: String? = null,
    val coverageAmount: Money? = null,
    val expDateText: String? = null,
    val creditor: String? = null,
    val principal: Money? = null,
    val locationText: String? = null,
    val location: String? = null,
    val deedNum: String? = null,
    val area: Double? = null,
    val symbol: String? = null,
    val typeName: String? = null,
    val type: String? = null,
    val image: String? = null,
    val updatedAt: String? = null,
)

data class MessageItem(
    val id: String? = null,
    val senderId: String? = null,
    val msgType: String? = null,
    val content: String? = null,
    val metadata: MessageMetadata? = null,
    val createdAt: String? = null,
    val senderName: String? = null,
    val senderImage: String? = null,
    val isMe: Boolean? = null,
)

data class MessageMetadata(
    val assetId: String? = null,
    val assetType: String? = null,
    val itemName: String? = null,
    val isDeleted: Boolean? = null,
    val shareAtDisplay: String? = null,
    val snapshotTitle: String? = null,
)

data class AcceptFriendRequest(
    val requesterId: String,
    val action: String,
)

data class AcceptFriendResult(
    val success: String? = null,
)

data class GroupSummary(
    val id: String? = null,
    val groupName: String? = null,
    val groupProfile: String? = null,
    val createdBy: String? = null,
    val memberCount: Int? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
)

data class GroupData(
    val id: String? = null,
    val groupName: String? = null,
    val groupProfile: String? = null,
    val createdBy: String? = null,
    val memberCount: Int? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
)

data class GroupMember(
    val id: String? = null,
    val username: String? = null,
    val email: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val profile: String? = null,
    val isFriend: Boolean? = null,
)

data class GroupResult(
    val status: String? = null,
    val data: GroupData? = null,
    val error: String? = null,
)

data class GroupMessage(
    val senderId: String? = null,
    val msgType: String? = null,
    val content: String? = null,
    val metadata: GroupMessageMetadata? = null,
    val createdAt: String? = null,
    val senderName: String? = null,
    val senderImage: String? = null,
    val isMe: Boolean? = null,
)

data class GroupMessageMetadata(
    val assetUrl: String? = null,
    val assetId: String? = null,
    val assetType: String? = null,
    val itemName: String? = null,
    val snapshotTitle: String? = null,
    val isActionRequired: Boolean? = null,
    val isCompleted: Boolean? = null,
    val shareAtDisplay: String? = null,
    val targetUserIds: List<String>? = null,
    val type: String? = null,
    val isDeleted: Boolean? = null,
)

data class GroupChatAction(
    val action: String,
    val groupId: String,
)

data class GrantAccess(
    val targetId: String? = null,
    val itemIds: List<String>? = null,
)

data class ShareAssetDetail(
    val id: String? = null,
    val name: String? = null,
    val amount: Money? = null,
    val type: String? = null,
    val image: String? = null,
    val locationText: String? = null,
    val bankName: String? = null,
    val accountNumber: String? = null,
    /** Deed numbers are identifiers; keeping them as text avoids numeric precision loss. */
    val deedNumber: String? = null,
    val area: Double? = null,
    val location: String? = null,
    val companyName: String? = null,
    val polNumber: String? = null,
    val coverageAmount: Money? = null,
    val expDateText: String? = null,
    val symbol: String? = null,
    val typeName: String? = null,
    val creditor: String? = null,
    val principal: Money? = null,
)

data class ShareGroup(
    val groupItemId: String? = null,
    val sharedBy: String? = null,
    val sharedAt: String? = null,
    val type: String? = null,
    val assetDetail: ShareAssetDetail? = null,
)

data class ShareFriend(
    val groupItemId: String? = null,
    val sharedBy: String? = null,
    val sharedAt: String? = null,
    val type: String? = null,
    val assetDetail: ShareAssetDetail? = null,
)

data class ShareableItem(
    val id: String? = null,
    val type: String? = null,
    val name: String? = null,
    val value: Money? = null,
    val image: String? = null,
    val isShared: Boolean? = null,
    val sharedAt: String? = null,
)

data class ShareTarget(val id: String? = null, val shareAt: String? = null)

data class ShareItems(
    val itemIds: String? = null,
    val itemTypes: String? = null,
    val emails: List<ShareTarget>? = null,
    val friends: List<ShareTarget>? = null,
    val groups: List<ShareTarget>? = null,
)

/** Presentation-facing share selection state.  It is domain-owned so routes
 * and summary screens do not depend on the legacy financial-common package. */
data class ShareTo(
    val email: List<ShareInfo> = emptyList(),
    val friend: List<ShareInfo> = emptyList(),
    val group: List<ShareInfo> = emptyList(),
    val shareAt: String = "",
)

data class ShareInfo(
    val name: String? = null,
    val userId: String = "",
    val date: String? = null,
    val apiDate: String? = null,
    val typeData: String = "",
    val subText: String = "",
    val profileUrl: String? = null,
)

data class GroupShareTarget(
    val groupId: String? = null,
    val groupName: String? = null,
    val groupImage: String? = null,
    val memberCount: Int? = null,
    val sharedAt: String? = null,
)

data class FriendShareTarget(
    val friendId: String? = null,
    val userName: String? = null,
    val profileImage: String? = null,
    val sharedAt: String? = null,
)

data class EmailShareTarget(
    val email: String? = null,
    val sharedAt: String? = null,
    val isSent: Boolean? = null,
)

data class ShareTargets(
    val groups: List<GroupShareTarget>? = null,
    val friends: List<FriendShareTarget>? = null,
    val emails: List<EmailShareTarget>? = null,
)
