package com.wealthvault.profile.data

import com.wealthvault.config.Config
import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.architecture.runSuspendAppCatching
import com.wealthvault.domain.profile.CloseFriendData
import com.wealthvault.domain.profile.FriendData
import com.wealthvault.domain.profile.UpdateUserData
import com.wealthvault.domain.profile.UpdateUserDataRequest
import com.wealthvault.domain.profile.UserData
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

internal interface ProfileRemoteDataSource {
    suspend fun getUser(): AppResult<UserData>
    suspend fun getCloseFriends(): AppResult<List<CloseFriendData>>
    suspend fun updateUserData(request: UpdateUserDataRequest): AppResult<UpdateUserData>
    suspend fun updateCloseFriendStatus(friendId: String, isClose: Boolean): AppResult<Boolean>
    suspend fun getAllFriends(): AppResult<List<FriendData>>
}

/**
 * Profile owns its wire contracts. API DTOs are deliberately private to this
 * data adapter so presentation/domain code cannot depend on backend JSON.
 */
internal class ProfileDataSource(
    private val client: HttpClient,
) : ProfileRemoteDataSource {
    override suspend fun getUser(): AppResult<UserData> = runSuspendAppCatching {
        val response: UserResponse = client.get("${Config.apiBaseUrl}user/").body()
        response.data?.toDomain() ?: error(response.error ?: "User is null, cannot create user")
    }

    override suspend fun getCloseFriends(): AppResult<List<CloseFriendData>> = runSuspendAppCatching {
        val response: CloseFriendResponse = client.get("${Config.apiBaseUrl}closefriend").body()
        if (response.error != null) error(response.error)
        response.data.orEmpty().map(RemoteCloseFriend::toDomain)
    }

    override suspend fun updateUserData(request: UpdateUserDataRequest): AppResult<UpdateUserData> =
        runSuspendAppCatching {
            val response: UpdateUserResponse = client.patch("${Config.apiBaseUrl}user") {
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append("username", request.username)
                            append("first_name", request.firstName)
                            append("last_name", request.lastName)
                            append("birthday", request.birthday)
                            append("phonenumber", request.phoneNumber)
                            request.profileImage?.let { imageBytes ->
                                append("profile_image", imageBytes, Headers.build {
                                    append(HttpHeaders.ContentType, "image/jpeg")
                                    append(HttpHeaders.ContentDisposition, "filename=\"profile.jpg\"")
                                })
                            }
                            request.sharedEnabled?.let { append("shared_enabled", it.toString()) }
                            request.sharedAge?.let { append("shared_age", it.toString()) }
                        },
                    ),
                )
            }.body()
            response.data?.toDomain() ?: error(response.error ?: "Update user response is empty")
        }

    override suspend fun updateCloseFriendStatus(friendId: String, isClose: Boolean): AppResult<Boolean> =
        runSuspendAppCatching {
            val response: UpdateCloseFriendResponse = client.post("${Config.apiBaseUrl}closefriend") {
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append("friend_id", friendId)
                            append("is_close", isClose.toString())
                        },
                    ),
                )
            }.body()
            response.data?.success ?: error(response.status ?: "Update close friend response is empty")
        }

    override suspend fun getAllFriends(): AppResult<List<FriendData>> = runSuspendAppCatching {
        val response: FriendResponse = client.get("${Config.apiBaseUrl}friend/").body()
        if (response.error != null) error(response.error)
        response.data?.friends.orEmpty().map(RemoteFriend::toDomain)
    }
}

@Serializable
private data class UserResponse(
    @SerialName("data") val data: RemoteUser? = null,
    @SerialName("error") val error: String? = null,
)

@Serializable
private data class RemoteUser(
    @SerialName("id") val id: String? = null,
    @SerialName("username") val username: String? = null,
    @SerialName("email") val email: String? = null,
    @SerialName("first_name") val firstName: String? = null,
    @SerialName("last_name") val lastName: String? = null,
    @SerialName("phone_number") val phoneNumber: String? = null,
    @SerialName("profile") val profile: String? = null,
    @SerialName("birthday") val birthday: String? = null,
    @SerialName("shared_age") val sharedAge: Int? = null,
    @SerialName("shared_enabled") val sharedEnabled: Boolean? = false,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("is_friend") val isFriend: Boolean? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
)

@Serializable
private data class FriendResponse(
    @SerialName("data") val data: FriendList? = null,
    @SerialName("error") val error: String? = null,
)

@Serializable
private data class FriendList(
    @SerialName("friends") val friends: List<RemoteFriend>? = null,
)

@Serializable
private data class RemoteFriend(
    @SerialName("id") val id: String? = null,
    @SerialName("username") val username: String? = null,
    @SerialName("email") val email: String? = null,
    @SerialName("first_name") val firstName: String? = null,
    @SerialName("last_name") val lastName: String? = null,
    @SerialName("phone_number") val phoneNumber: String? = null,
    @SerialName("profile") val profile: String? = null,
    @SerialName("birthday") val birthday: String? = null,
    @SerialName("shared_age") val sharedAge: Int? = null,
    @SerialName("share_enabled") val shareEnabled: Boolean? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("is_friend") val isFriend: Boolean? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
)

@Serializable
private data class CloseFriendResponse(
    @SerialName("data") val data: List<RemoteCloseFriend>? = null,
    @SerialName("error") val error: String? = null,
)

@Serializable
private data class RemoteCloseFriend(
    @SerialName("id") val id: String,
    @SerialName("username") val username: String,
    @SerialName("email") val email: String,
    @SerialName("first_name") val firstName: String,
    @SerialName("last_name") val lastName: String,
    @SerialName("phone_number") val phoneNumber: String,
    @SerialName("profile") val profile: String,
    @SerialName("birthday") val birthday: String,
    @SerialName("shared_age") val sharedAge: Int,
    @SerialName("shared_enabled") val sharedEnabled: Boolean,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String,
    @SerialName("is_close") val isClose: Boolean,
)

@Serializable
private data class UpdateUserResponse(
    @SerialName("data") val data: RemoteUpdatedUser? = null,
    @SerialName("error") val error: String? = null,
)

@Serializable
private data class RemoteUpdatedUser(
    @SerialName("id") val id: String? = null,
    @SerialName("username") val username: String? = null,
    @SerialName("email") val email: String? = null,
    @SerialName("first_name") val firstName: String? = null,
    @SerialName("last_name") val lastName: String? = null,
    @SerialName("phone_number") val phoneNumber: String? = null,
    @SerialName("profile") val profile: String? = null,
    @SerialName("birthday") val birthday: String? = null,
    @SerialName("shared_age") val sharedAge: Int? = null,
    @SerialName("share_enabled") val shareEnabled: Boolean? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
)

@Serializable
private data class UpdateCloseFriendResponse(
    @SerialName("data") val data: UpdateCloseFriendResult? = null,
    @SerialName("status") val status: String? = null,
)

@Serializable
private data class UpdateCloseFriendResult(
    @SerialName("success") val success: Boolean? = null,
)

private fun RemoteUser.toDomain() = UserData(
    id = id,
    username = username,
    email = email,
    firstName = firstName,
    lastName = lastName,
    phoneNumber = phoneNumber,
    profile = profile,
    birthday = birthday,
    sharedAge = sharedAge,
    shareEnabled = sharedEnabled,
    createdAt = createdAt,
    isFriend = isFriend,
    updatedAt = updatedAt,
)

private fun RemoteFriend.toDomain() = FriendData(
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

private fun RemoteCloseFriend.toDomain() = CloseFriendData(
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

private fun RemoteUpdatedUser.toDomain() = UpdateUserData(
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
)
