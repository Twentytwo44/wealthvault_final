package com.wealthvault.data.profile.repository

import com.wealthvault.domain.profile.CloseFriendData
import com.wealthvault.domain.profile.FriendData
import com.wealthvault.domain.profile.UserData
import kotlinx.serialization.Serializable

/**
 * Persistence-only snapshots for profile read models.
 *
 * Domain contracts intentionally do not carry a serialization dependency. Keep
 * the cache schema in the data module so a backend or storage format change
 * cannot leak into presentation/domain packages.
 */
@Serializable
internal data class UserCacheRecord(
    val id: String? = null,
    val username: String? = null,
    val email: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val phoneNumber: String? = null,
    val profile: String? = null,
    val birthday: String? = null,
    val sharedAge: Int? = null,
    val shareEnabled: Boolean? = false,
    val createdAt: String? = null,
    val isFriend: Boolean? = null,
    val updatedAt: String? = null,
)

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
    val isFriend: Boolean? = null,
    val updatedAt: String? = null,
)

@Serializable
internal data class CloseFriendCacheRecord(
    val id: String,
    val username: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val profile: String,
    val birthday: String,
    val sharedAge: Int,
    val sharedEnabled: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val isClose: Boolean,
)

internal fun UserData.toCacheRecord() = UserCacheRecord(
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
    isFriend = isFriend,
    updatedAt = updatedAt,
)

internal fun UserCacheRecord.toDomain() = UserData(
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
    isFriend = isFriend,
    updatedAt = updatedAt,
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
    isFriend = isFriend,
    updatedAt = updatedAt,
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
    isFriend = isFriend,
    updatedAt = updatedAt,
)

internal fun CloseFriendData.toCacheRecord() = CloseFriendCacheRecord(
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
    isClose = isClose,
)

internal fun CloseFriendCacheRecord.toDomain() = CloseFriendData(
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
    isClose = isClose,
)
