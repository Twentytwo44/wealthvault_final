package com.wealthvault.domain.profile

import kotlin.test.Test
import kotlin.test.assertEquals

class ProfileContractsTest {
    @Test
    fun profileModelsCarryNullableBackendFieldsWithoutTransportTypes() {
        val user = UserData(
            id = "user-1",
            username = "alice",
            email = "alice@example.com",
            firstName = "Alice",
            lastName = "Example",
            phoneNumber = "0812345678",
            profile = "avatar",
            birthday = "1990-01-01",
            sharedAge = 31,
            shareEnabled = true,
            createdAt = "2026-01-01",
            isFriend = true,
            updatedAt = "2026-01-02",
        )
        assertEquals("user-1", user.id)
        assertEquals(true, user.shareEnabled)
        assertEquals(UserData(), UserData())

        val friend = FriendData(username = "bob", isFriend = false, shareEnabled = true)
        assertEquals("bob", friend.username)
        assertEquals(false, friend.isFriend)
        assertEquals(true, friend.shareEnabled)

        val closeFriend = CloseFriendData(
            "friend-1", "bob", "bob@example.com", "Bob", "Friend", "0800000000",
            "avatar", "1991-02-02", 32, true, "2026-01-01", "2026-01-02", true,
        )
        assertEquals("friend-1", closeFriend.id)
        assertEquals(true, closeFriend.isClose)

        val request = UpdateUserDataRequest(
            username = "alice",
            firstName = "Alice",
            lastName = "Example",
            birthday = "1990-01-01",
            phoneNumber = "0812345678",
            profileImage = byteArrayOf(1, 2),
            sharedEnabled = true,
            sharedAge = 31,
        )
        assertEquals("alice", request.username)
        assertEquals(2, request.profileImage?.size)

        val updated = UpdateUserData(id = "user-1", username = "alice", shareEnabled = true)
        assertEquals("user-1", updated.id)
        assertEquals(true, updated.shareEnabled)

        val line = LineUser(
            userId = "line-1",
            displayName = "Alice",
            pictureUrl = "https://example.test/avatar",
            accessToken = "token",
            statusMessage = "hello",
            idToken = "id-token",
        )
        assertEquals("line-1", line.userId)
        assertEquals("Alice", line.displayName)
        assertEquals(LineUser("line-1", "Alice"), LineUser("line-1", "Alice"))
    }
}
