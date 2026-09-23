package com.wealthvault.domain.profile

import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.architecture.CachedValue
import kotlinx.coroutines.flow.Flow

data class UserData(
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

/** Compatibility name; the shared read model is owned by core:model. */
typealias FriendData = com.wealthvault.core.model.FriendData

data class CloseFriendData(
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

data class UpdateUserDataRequest(
    val username: String,
    val firstName: String,
    val lastName: String,
    val birthday: String,
    val phoneNumber: String,
    val profileImage: ByteArray? = null,
    val sharedEnabled: Boolean? = null,
    val sharedAge: Int? = null,
)

data class UpdateUserData(
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
)

/** LINE SDK result exposed to presentation through a domain contract. */
data class LineUser(
    val userId: String,
    val displayName: String,
    val pictureUrl: String? = null,
    val accessToken: String? = null,
    val statusMessage: String? = null,
    val idToken: String? = null,
)

/** Platform-agnostic interactive LINE sign-in capability. */
interface LineSignInProvider {
    fun login()
}

interface ProfileRepository {
    suspend fun getUser(force: Boolean = false): AppResult<UserData>
    suspend fun getCloseFriends(force: Boolean = false): AppResult<List<CloseFriendData>>
    suspend fun updateUserData(request: UpdateUserDataRequest): AppResult<UpdateUserData>
    suspend fun setCloseFriend(friendId: String, isClose: Boolean): AppResult<Boolean>
    suspend fun getAllFriends(force: Boolean = false): AppResult<List<FriendData>>

    /** Emits the last usable snapshot and its freshness without exposing storage details. */
    fun observeUser(): Flow<CachedValue<UserData>>
    fun observeCloseFriends(): Flow<CachedValue<List<CloseFriendData>>>
    fun observeAllFriends(): Flow<CachedValue<List<FriendData>>>

    /** Refreshes all profile read models; manual refresh bypasses TTL. */
    suspend fun refresh(force: Boolean = false): AppResult<Unit>
}

/** Minimal profile lookup needed by authentication routing. */
interface CurrentUserRepository {
    suspend fun getUser(): AppResult<UserData>
}

/** Profile settings depend on capabilities, not on the LINE API DTOs. */
interface LineLinkRepository {
    suspend fun link(idToken: String): AppResult<Unit>
}

interface DeviceRegistrationRepository {
    suspend fun unregister(token: String): AppResult<Unit>
}

/** Friend lookup used by portfolio sharing forms. */
interface FriendDirectoryRepository {
    suspend fun getFriend(): AppResult<List<FriendData>>
}
