package com.wealthvault.user_api

import com.wealthvault.core.model.DashboardData
import com.wealthvault.core.model.DashboardItem
import com.wealthvault.core.model.DashboardNetWorth
import com.wealthvault.core.model.Money
import com.wealthvault.domain.profile.FriendData
import com.wealthvault.domain.profile.CloseFriendData
import com.wealthvault.domain.profile.UpdateUserData
import com.wealthvault.domain.profile.UserData
import com.wealthvault.domain.social.AssetDetailPreview
import com.wealthvault.domain.social.FriendProfile
import com.wealthvault.domain.social.FriendUserInfo
import com.wealthvault.domain.social.ItemPreview
import com.wealthvault.domain.social.MessageItem
import com.wealthvault.domain.social.MessageMetadata
import com.wealthvault.domain.social.PendingFriend
import com.wealthvault.`user-api`.model.DashboardDataResponse
import com.wealthvault.`user-api`.model.DashboardItem as DashboardWireItem
import com.wealthvault.`user-api`.model.FriendData as FriendWireData
import com.wealthvault.`user-api`.model.FriendProfileResponse
import com.wealthvault.`user-api`.model.FriendUserInfo as FriendUserWireInfo
import com.wealthvault.`user-api`.model.ItemPreview as ItemPreviewWire
import com.wealthvault.`user-api`.model.AssetDetailPreview as AssetDetailWire
import com.wealthvault.`user-api`.model.FriendResponse
import com.wealthvault.`user-api`.model.MessageItem as MessageWireItem
import com.wealthvault.`user-api`.model.MessageResponse
import com.wealthvault.`user-api`.model.MessageMetadata as MessageMetadataWire
import com.wealthvault.`user-api`.model.PendingFriendData as PendingWireData
import com.wealthvault.`user-api`.model.PendingFriendResponse
import com.wealthvault.`user-api`.model.SearchUserResponse
import com.wealthvault.`user-api`.model.UserDataResponse
import com.wealthvault.`user-api`.model.CloseFriendResponse
import com.wealthvault.`user-api`.model.UpdateUserDataResponse

internal fun FriendResponse.requireDomainFriends(): List<FriendData> {
    error?.let { throw IllegalStateException(it) }
    return data?.friend.orEmpty().map(FriendWireData::toDomain)
}

internal fun SearchUserResponse.requireDomainUsers(): List<FriendData> {
    error?.let { throw IllegalStateException(it) }
    return data.orEmpty().map(FriendWireData::toDomain)
}

internal fun UserDataResponse.requireDomainUser(): UserData =
    data?.toDomain() ?: throw IllegalStateException(error ?: "User response did not contain data")

internal fun CloseFriendResponse.requireDomainCloseFriends(): List<CloseFriendData> {
    error?.let { throw IllegalStateException(it) }
    return data.orEmpty().map { friend ->
        CloseFriendData(
            id = friend.id,
            username = friend.username,
            email = friend.email,
            firstName = friend.firstName,
            lastName = friend.lastName,
            phoneNumber = friend.phoneNumber,
            profile = friend.profile,
            birthday = friend.birthday,
            sharedAge = friend.sharedAge,
            sharedEnabled = friend.sharedEnabled,
            createdAt = friend.createdAt,
            updatedAt = friend.updatedAt,
            isClose = friend.isClose,
        )
    }
}

internal fun UpdateUserDataResponse.requireDomainUpdatedUser(): UpdateUserData =
    data?.let { updated ->
        UpdateUserData(
            id = updated.id,
            username = updated.username,
            email = updated.email,
            firstName = updated.firstName,
            lastName = updated.lastName,
            phoneNumber = updated.phoneNumber,
            profile = updated.profile,
            birthday = updated.birthday,
            sharedAge = updated.sharedAge,
            shareEnabled = updated.shareEnabled,
            createdAt = updated.createdAt,
            updatedAt = updated.updatedAt,
        )
    } ?: throw IllegalStateException(error ?: "Updated user response did not contain data")

internal fun PendingFriendResponse.requireDomainPendingFriends(): List<PendingFriend> {
    error?.let { throw IllegalStateException(it) }
    return data?.friends.orEmpty().map(PendingWireData::toDomain)
}

internal fun FriendProfileResponse.requireDomainProfile(): FriendProfile =
    data?.toDomain() ?: throw IllegalStateException("Friend profile response did not contain data")

internal fun MessageResponse.toDomainMessages(): List<MessageItem> =
    messages.orEmpty().map(MessageWireItem::toDomain)

internal fun DashboardDataResponse.toDomain(): DashboardData = DashboardData(
    assets = assets.map(DashboardWireItem::toDomain),
    friendCount = friendCount,
    liabilities = liabilities.map(DashboardWireItem::toDomain),
    netWorth = netWorth?.let { value ->
        DashboardNetWorth(
            count = value.count,
            totalAssets = Money.fromDouble(value.totalAssets) ?: Money(0),
            totalLiabilities = Money.fromDouble(value.totalLiabilities) ?: Money(0),
            value = Money.fromDouble(value.value) ?: Money(0),
        )
    },
    uniqueSharedItemCount = uniqueSharedItemCount,
)

private fun FriendWireData.toDomain() = FriendData(
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

private fun com.wealthvault.`user-api`.model.UserData.toDomain() = UserData(
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

private fun PendingWireData.toDomain() = PendingFriend(
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

private fun com.wealthvault.`user-api`.model.FriendProfileData.toDomain() = FriendProfile(
    userInfo = userInfo?.toDomain(),
    itemPreview = itemPreview.orEmpty().map(ItemPreviewWire::toDomain),
)

private fun FriendUserWireInfo.toDomain() = FriendUserInfo(
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

private fun ItemPreviewWire.toDomain() = ItemPreview(
    itemId = itemId,
    type = type,
    assetDetail = assetDetail?.toDomain(),
)

private fun AssetDetailWire.toDomain() = AssetDetailPreview(
    id = id,
    name = name,
    bankName = bankName,
    accountNumber = accountNumber,
    amount = amount?.let(Money::fromDouble),
    companyName = companyName,
    polNum = polNum,
    coverageAmount = coverageAmount?.let(Money::fromDouble),
    expDateText = expDateText,
    creditor = creditor,
    principal = principal?.let(Money::fromDouble),
    locationText = locationText,
    location = location,
    deedNum = deedNum,
    area = area,
    symbol = symbol,
    typeName = typeName,
    type = type,
    image = null,
    updatedAt = null,
)

private fun MessageWireItem.toDomain() = MessageItem(
    id = id,
    senderId = senderId,
    msgType = msgType,
    content = content,
    metadata = metadata?.toDomain(),
    createdAt = createdAt,
    senderName = senderName,
    senderImage = senderImage,
    isMe = isMe,
)

private fun MessageMetadataWire.toDomain() = MessageMetadata(
    assetId = assetId,
    assetType = assetType,
    itemName = itemName,
    isDeleted = isDeleted,
    shareAtDisplay = shareAtDisplay,
    snapshotTitle = snapshotTitle,
)

private fun DashboardWireItem.toDomain() = DashboardItem(
    id = id,
    type = type,
    name = name,
    value = Money.fromDouble(value),
    createdAt = createdAt,
)
