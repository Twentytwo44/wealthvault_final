package com.wealthvault.data.social.share.transport

import com.wealthvault.core.model.Money
import com.wealthvault.domain.social.EmailShareTarget
import com.wealthvault.domain.social.FriendShareTarget
import com.wealthvault.domain.social.GroupShareTarget
import com.wealthvault.domain.social.ItemPreview
import com.wealthvault.domain.social.ShareAssetDetail
import com.wealthvault.domain.social.ShareFriend
import com.wealthvault.domain.social.ShareGroup
import com.wealthvault.domain.social.ShareItems
import com.wealthvault.domain.social.ShareTarget
import com.wealthvault.domain.social.ShareTargets
import com.wealthvault.domain.social.ShareableItem
import com.wealthvault.data.social.share.transport.model.AssetDetail as ShareWireAssetDetail
import com.wealthvault.data.social.share.transport.model.EmailDataList
import com.wealthvault.data.social.share.transport.model.FriendDataList
import com.wealthvault.data.social.share.transport.model.GroupDataList
import com.wealthvault.data.social.share.transport.model.ItemShareTargetsResponse
import com.wealthvault.data.social.share.transport.model.ItemToShareData
import com.wealthvault.data.social.share.transport.model.ItemToShareResponse
import com.wealthvault.data.social.share.transport.model.ShareFriendData
import com.wealthvault.data.social.share.transport.model.ShareFriendResponse
import com.wealthvault.data.social.share.transport.model.ShareGroupData
import com.wealthvault.data.social.share.transport.model.ShareGroupResponse
import com.wealthvault.data.social.share.transport.model.ShareItemRequest
import com.wealthvault.data.social.share.transport.model.ShareItemResponse
import com.wealthvault.data.social.share.transport.model.TargetItem

internal fun ShareItems.toWire(): ShareItemRequest = ShareItemRequest(
    itemIds = itemIds,
    itemTypes = itemTypes,
    emails = emails?.map(ShareTarget::toWire),
    friends = friends?.map(ShareTarget::toWire),
    groups = groups?.map(ShareTarget::toWire),
)

private fun ShareTarget.toWire() = TargetItem(id = id, shareAt = shareAt)

internal fun ShareItemResponse.requireSuccess(): Boolean {
    error?.let { throw IllegalStateException(it) }
    return data ?: true
}

internal fun ItemToShareResponse.requireDomainData(): List<ShareableItem> {
    error?.let { throw IllegalStateException(it) }
    return items.orEmpty().map(ItemToShareData::toDomain)
}

internal fun ShareFriendResponse.requireDomainData(): List<ShareFriend> {
    error?.let { throw IllegalStateException(it) }
    return data.orEmpty().map(ShareFriendData::toDomain)
}

internal fun ShareGroupResponse.requireDomainData(): List<ShareGroup> {
    error?.let { throw IllegalStateException(it) }
    return data.orEmpty().map(ShareGroupData::toDomain)
}

internal fun ItemShareTargetsResponse.requireDomainData(): ShareTargets {
    if (error != null) throw IllegalStateException("Unable to load share targets")
    return ShareTargets(
        groups = groups?.map(GroupDataList::toDomain),
        friends = friends?.map(FriendDataList::toDomain),
        emails = emails?.map(EmailDataList::toDomain),
    )
}

private fun ItemToShareData.toDomain() = ShareableItem(
    id = id,
    type = type,
    name = name,
    value = Money.fromDouble(value),
    image = image,
    isShared = isShared,
    sharedAt = sharedAt,
)

private fun ShareFriendData.toDomain() = ShareFriend(
    groupItemId = groupItemId,
    sharedBy = sharedBy,
    sharedAt = sharedAt,
    type = type,
    assetDetail = assetDetail?.toDomain(),
)

private fun ShareGroupData.toDomain() = ShareGroup(
    groupItemId = groupItemId,
    sharedBy = sharedBy,
    sharedAt = sharedAt,
    type = type,
    assetDetail = assetDetail?.toDomain(),
)

private fun ShareWireAssetDetail.toDomain() = ShareAssetDetail(
    id = id,
    name = name,
    amount = Money.fromDouble(amount),
    type = type,
    image = image,
    locationText = locationText,
    bankName = bankName,
    accountNumber = accountNumber,
    deedNumber = deedNumber?.toString(),
    area = area,
    location = location,
    companyName = companyName,
    polNumber = polNumber,
    coverageAmount = Money.fromDouble(coverageAmount),
    expDateText = expDateText,
    symbol = symbol,
    typeName = typeName,
    creditor = creditor,
    principal = Money.fromDouble(principal),
)

private fun GroupDataList.toDomain() = GroupShareTarget(
    groupId = groupId,
    groupName = groupName,
    groupImage = groupImage,
    memberCount = memberCount,
    sharedAt = sharedAt,
)

private fun FriendDataList.toDomain() = FriendShareTarget(
    friendId = friendId,
    userName = userName,
    profileImage = profileImage,
    sharedAt = sharedAt,
)

private fun EmailDataList.toDomain() = EmailShareTarget(
    email = email,
    sharedAt = sharedAt,
    isSent = isSent,
)
