package com.wealthvault.data.social.repository

import com.wealthvault.core.model.FriendData
import com.wealthvault.domain.social.GroupSummary
import com.wealthvault.domain.social.PendingFriend
import kotlinx.serialization.Serializable

/** Persistence-only records for social read-model snapshots. */
@Serializable
internal data class FriendCacheRecord(
    val id: String? = null,
    val username: String? = null,
    val email: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val phoneNumber: String? = null,
    val profile: String? = null,
    val birthday: String? = null,
    val sharedAge: Int? = null,
    val shareEnabled: Boolean? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val isFriend: Boolean? = null,
)

@Serializable
internal data class GroupSummaryCacheRecord(
    val id: String? = null,
    val groupName: String? = null,
    val groupProfile: String? = null,
    val createdBy: String? = null,
    val memberCount: Int? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
)

@Serializable
internal data class PendingFriendCacheRecord(
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

internal fun FriendData.toCacheRecord() = FriendCacheRecord(
    id = id,
    username = username,
    email = email,
    firstName = firstName,
    lastName = lastName,
    phoneNumber = phoneNumber,
    profile = profile,
    birthday = birthday,
    sharedAge = sharedAge,
    shareEnabled = shareEnabled,
    createdAt = createdAt,
    updatedAt = updatedAt,
    isFriend = isFriend,
)

internal fun FriendCacheRecord.toDomain() = FriendData(
    id = id,
    username = username,
    email = email,
    firstName = firstName,
    lastName = lastName,
    phoneNumber = phoneNumber,
    profile = profile,
    birthday = birthday,
    sharedAge = sharedAge,
    shareEnabled = shareEnabled,
    createdAt = createdAt,
    updatedAt = updatedAt,
    isFriend = isFriend,
)

internal fun GroupSummary.toCacheRecord() = GroupSummaryCacheRecord(
    id = id,
    groupName = groupName,
    groupProfile = groupProfile,
    createdBy = createdBy,
    memberCount = memberCount,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

internal fun GroupSummaryCacheRecord.toDomain() = GroupSummary(
    id = id,
    groupName = groupName,
    groupProfile = groupProfile,
    createdBy = createdBy,
    memberCount = memberCount,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

internal fun PendingFriend.toCacheRecord() = PendingFriendCacheRecord(
    id = id,
    username = username,
    email = email,
    firstName = firstName,
    lastName = lastName,
    phoneNumber = phoneNumber,
    profile = profile,
    birthday = birthday,
    sharedAge = sharedAge,
    sharedEnabled = sharedEnabled,
    createdAt = createdAt,
    updatedAt = updatedAt,
    isFriend = isFriend,
    isClose = isClose,
)

internal fun PendingFriendCacheRecord.toDomain() = PendingFriend(
    id = id,
    username = username,
    email = email,
    firstName = firstName,
    lastName = lastName,
    phoneNumber = phoneNumber,
    profile = profile,
    birthday = birthday,
    sharedAge = sharedAge,
    sharedEnabled = sharedEnabled,
    createdAt = createdAt,
    updatedAt = updatedAt,
    isFriend = isFriend,
    isClose = isClose,
)
