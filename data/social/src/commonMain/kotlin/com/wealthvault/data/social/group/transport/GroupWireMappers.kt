package com.wealthvault.data.social.group.transport

import com.wealthvault.domain.social.GroupData
import com.wealthvault.domain.social.GroupMember
import com.wealthvault.domain.social.GroupMessage
import com.wealthvault.domain.social.GroupMessageMetadata
import com.wealthvault.domain.social.GroupResult
import com.wealthvault.domain.social.GrantAccess
import com.wealthvault.data.social.group.transport.model.DeleteGroupResponse
import com.wealthvault.data.social.group.transport.model.GroupData as GroupWireData
import com.wealthvault.data.social.group.transport.model.GroupMemberResponse
import com.wealthvault.data.social.group.transport.model.GroupMsgData
import com.wealthvault.data.social.group.transport.model.GroupMsgResponse
import com.wealthvault.data.social.group.transport.model.GroupResponse
import com.wealthvault.data.social.group.transport.model.GrantAccessRequest
import com.wealthvault.data.social.group.transport.model.MemberResponse
import com.wealthvault.data.social.group.transport.model.grantaccess

internal fun GrantAccess.toWire() = GrantAccessRequest(
    targetId = targetId,
    itemIds = itemIds,
)

internal fun GroupResponse.requireDomainResult(): GroupResult {
    error?.let { throw IllegalStateException(it) }
    return GroupResult(status = status, data = data?.toDomain(), error = null)
}

internal fun DeleteGroupResponse.requireSuccess(): Boolean {
    error?.let { throw IllegalStateException(it) }
    return data ?: true
}

internal fun MemberResponse.requireSuccess(): Boolean {
    error?.let { throw IllegalStateException(it) }
    return true
}

internal fun GroupMemberResponse.requireDomainMembers(): List<GroupMember> {
    error?.let { throw IllegalStateException(it) }
    return data?.members.orEmpty().map { member ->
        GroupMember(
            id = member.id,
            username = member.username,
            email = member.email,
            firstName = member.firstName,
            lastName = member.lastName,
            profile = member.profile,
            isFriend = member.isFriend,
        )
    }
}

internal fun GroupMsgResponse.requireDomainMessages(): List<GroupMessage> {
    error?.let { throw IllegalStateException(it) }
    return data.map(GroupMsgData::toDomain)
}

internal fun grantaccess.requireSuccess(): Boolean {
    error?.let { throw IllegalStateException(it) }
    return data ?: true
}

private fun GroupWireData.toDomain() = GroupData(
    id = id,
    groupName = groupName,
    groupProfile = groupProfile,
    createdBy = createdBy,
    memberCount = memberCount,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

private fun GroupMsgData.toDomain() = GroupMessage(
    senderId = senderId,
    msgType = msgType,
    content = content,
    metadata = metadata?.let {
        GroupMessageMetadata(
            assetUrl = it.assetUrl,
            assetId = it.assetId,
            assetType = it.assetType,
            itemName = it.itemName,
            snapshotTitle = it.snapshotTitle,
            isActionRequired = it.isActionRequired,
            isCompleted = it.isCompleted,
            shareAtDisplay = it.shareAtDisplay,
            targetUserIds = it.targetUserIds,
            type = it.type,
            isDeleted = it.isDeleted,
        )
    },
    createdAt = createdAt,
    senderName = senderName,
    senderImage = senderImage,
    isMe = isMe,
)
