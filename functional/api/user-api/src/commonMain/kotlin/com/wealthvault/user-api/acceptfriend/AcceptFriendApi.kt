package com.wealthvault.`user-api`.acceptfriend

interface AcceptFriendApi {
    /**
     * Accepts or rejects a friend request without exposing the wire DTO to
     * feature/data consumers. The API implementation owns serialization and
     * response parsing; callers receive only the transport-level outcome.
     */
    suspend fun acceptFriend(requesterId: String, action: String): String?
}
