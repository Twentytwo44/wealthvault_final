package com.wealthvault.domain.social

import com.wealthvault.core.model.Money
import kotlin.test.Test
import kotlin.test.assertEquals

class SocialModelsTest {
    @Test
    fun socialModelsComposeNestedDomainValues() {
        val money = Money(12_345)
        val metadata = MessageMetadata(assetId = "asset-1", itemName = "Savings")
        val detail = AssetDetailPreview(id = "asset-1", name = "Savings", amount = money)
        val user = FriendUserInfo(id = "friend-1", username = "bob", isFriend = true)
        val profile = FriendProfile(userInfo = user, itemPreview = listOf(ItemPreview("item-1", "cash", detail)))

        assertEquals("friend-1", profile.userInfo?.id)
        assertEquals(money, profile.itemPreview.single().assetDetail?.amount)
        assertEquals("asset-1", metadata.assetId)

        val message = MessageItem(
            id = "message-1",
            senderId = "friend-1",
            msgType = "text",
            content = "hello",
            metadata = metadata,
            senderName = "Bob",
            isMe = false,
        )
        assertEquals(false, message.isMe)
        assertEquals("Savings", message.metadata?.itemName)

        val group = GroupData(id = "group-1", groupName = "Family", memberCount = 2)
        val result = GroupResult(status = "success", data = group)
        assertEquals("Family", result.data?.groupName)
        assertEquals(GroupSummary(id = "group-1", groupName = "Family", memberCount = 2), GroupSummary(id = "group-1", groupName = "Family", memberCount = 2))

        val groupMetadata = GroupMessageMetadata(
            assetId = "asset-1",
            assetType = "cash",
            itemName = "Savings",
            targetUserIds = listOf("friend-1"),
            isActionRequired = true,
        )
        val groupMessage = GroupMessage(
            senderId = "friend-1",
            msgType = "share",
            content = "shared",
            metadata = groupMetadata,
            isMe = false,
        )
        assertEquals(listOf("friend-1"), groupMessage.metadata?.targetUserIds)
        assertEquals(GroupChatAction("open", "group-1"), GroupChatAction("open", "group-1"))

        assertEquals(AcceptFriendRequest("friend-1", "accept"), AcceptFriendRequest("friend-1", "accept"))
        assertEquals(AcceptFriendResult("accepted"), AcceptFriendResult("accepted"))
        assertEquals(GrantAccess("friend-1", listOf("asset-1")), GrantAccess("friend-1", listOf("asset-1")))
    }

    @Test
    fun shareModelsKeepTargetsAndOptionalValuesImmutable() {
        val target = ShareTarget("friend-1", "2026-01-01")
        val shareItems = ShareItems(
            itemIds = "asset-1",
            itemTypes = "cash",
            emails = listOf(target),
            friends = listOf(target),
            groups = listOf(target),
        )
        assertEquals("asset-1", shareItems.itemIds)
        assertEquals(1, shareItems.groups?.size)

        val shareTo = ShareTo(
            friend = listOf(ShareInfo(name = "Bob", userId = "friend-1", typeData = "friend")),
            group = listOf(ShareInfo(name = "Family", userId = "group-1", typeData = "group")),
            shareAt = "2026-01-01",
        )
        assertEquals("Bob", shareTo.friend.single().name)
        assertEquals("group-1", shareTo.group.single().userId)

        val asset = ShareAssetDetail(id = "asset-1", name = "Savings", amount = Money(100))
        assertEquals(Money(100), asset.amount)
        assertEquals(ShareGroup(assetDetail = asset), ShareGroup(assetDetail = asset))
        assertEquals(ShareFriend(assetDetail = asset), ShareFriend(assetDetail = asset))
        assertEquals(ShareableItem(id = "asset-1", value = Money(100), isShared = true), ShareableItem(id = "asset-1", value = Money(100), isShared = true))

        val targets = ShareTargets(
            groups = listOf(GroupShareTarget(groupId = "group-1", groupName = "Family", memberCount = 2)),
            friends = listOf(FriendShareTarget(friendId = "friend-1", userName = "Bob")),
            emails = listOf(EmailShareTarget(email = "bob@example.com", isSent = true)),
        )
        assertEquals(2, targets.groups?.single()?.memberCount)
        assertEquals("Bob", targets.friends?.single()?.userName)
        assertEquals(true, targets.emails?.single()?.isSent)
        assertEquals(PendingFriend(username = "bob", isFriend = false, isClose = false), PendingFriend(username = "bob", isFriend = false, isClose = false))
        assertEquals(GroupMember(id = "friend-1", username = "bob", isFriend = true), GroupMember(id = "friend-1", username = "bob", isFriend = true))
    }
}
