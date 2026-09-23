package com.wealthvault.social.data

import com.wealthvault.config.Config
import com.wealthvault.core.model.Money
import com.wealthvault.core.model.FriendData
import com.wealthvault.domain.social.AcceptFriendRequest
import com.wealthvault.domain.social.AssetDetailPreview
import com.wealthvault.domain.social.FriendProfile
import com.wealthvault.domain.social.FriendUserInfo
import com.wealthvault.domain.social.ItemPreview
import com.wealthvault.domain.social.MessageItem
import com.wealthvault.domain.social.MessageMetadata
import com.wealthvault.domain.social.PendingFriend
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Social user transport contract; callers only see domain values. */
internal interface SocialUserTransport {
    suspend fun searchUser(email: String): List<FriendData>
    suspend fun addFriend(targetId: String): Boolean
    suspend fun deleteFriend(targetId: String): Boolean
    suspend fun getFriendMessages(friendId: String): List<MessageItem>
    suspend fun getFriendProfile(friendId: String): FriendProfile
    suspend fun acceptFriend(request: AcceptFriendRequest): String?
    suspend fun getPendingFriends(): List<PendingFriend>
}

internal class KtorSocialUserTransport(
    private val client: HttpClient,
) : SocialUserTransport {
    override suspend fun searchUser(email: String): List<FriendData> {
        val response = client.post("${Config.localhost_android}user/search") {
            setBody(MultiPartFormDataContent(formData { append("email", email) }))
        }.body<SearchUserWireResponse>()
        response.error?.let { throw IllegalStateException(it) }
        return response.data.orEmpty().map(SearchFriendWireData::toDomain)
    }

    override suspend fun addFriend(targetId: String): Boolean {
        val response = client.post("${Config.localhost_android}friend") {
            setBody(
                MultiPartFormDataContent(
                    formData { append("requester_id", targetId) },
                ),
            )
        }.body<FriendMutationWireResponse>()
        response.error?.let { throw IllegalStateException(it) }
        return true
    }

    override suspend fun deleteFriend(targetId: String): Boolean {
        val response = client.delete("${Config.localhost_android}friend/$targetId") {
            contentType(ContentType.Application.Json)
        }.body<DeleteFriendWireResponse>()
        response.error?.let { throw IllegalStateException(it) }
        return response.data?.success ?: true
    }

    override suspend fun getFriendMessages(friendId: String): List<MessageItem> = client
        .get("${Config.localhost_android}friend/$friendId/msg/")
        .body<MessageWireResponse>()
        .messages.orEmpty()
        .map(MessageWireItem::toDomain)

    override suspend fun getFriendProfile(friendId: String): FriendProfile {
        val response = client
            .get("${Config.localhost_android}friend/$friendId/profile")
            .body<FriendProfileWireResponse>()
        return response.data?.toDomain()
            ?: throw IllegalStateException("Friend profile response did not contain data")
    }

    override suspend fun acceptFriend(request: AcceptFriendRequest): String? {
        val response = client.post("${Config.localhost_android}friend/accept") {
            setBody(
                AcceptFriendWireRequest(
                    requesterId = request.requesterId,
                    action = request.action,
                ),
            )
        }.body<FriendMutationWireResponse>()
        response.error?.let { throw IllegalStateException(it) }
        return response.data?.success
    }

    override suspend fun getPendingFriends(): List<PendingFriend> {
        val response = client
            .get("${Config.localhost_android}friend/pending")
            .body<PendingFriendWireResponse>()
        response.error?.let { throw IllegalStateException(it) }
        return response.data?.friends.orEmpty().map(PendingFriendWireData::toDomain)
    }
}

@Serializable
internal data class SearchUserWireResponse(
    @SerialName("data") val data: List<SearchFriendWireData>? = null,
    @SerialName("error") val error: String? = null,
)

@Serializable
internal data class SearchFriendWireData(
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
internal data class FriendMutationWireResponse(
    @SerialName("data") val data: FriendMutationWireData? = null,
    @SerialName("error") val error: String? = null,
)

@Serializable
internal data class FriendMutationWireData(
    @SerialName("success") val success: String? = null,
)

@Serializable
internal data class DeleteFriendWireResponse(
    @SerialName("data") val data: DeleteFriendWireData? = null,
    @SerialName("error") val error: String? = null,
)

@Serializable
internal data class DeleteFriendWireData(
    @SerialName("success") val success: Boolean? = null,
)

@Serializable
internal data class AcceptFriendWireRequest(
    @SerialName("requester_id") val requesterId: String,
    @SerialName("action") val action: String,
)

@Serializable
internal data class MessageWireResponse(
    @SerialName("messages") val messages: List<MessageWireItem>? = null,
)

@Serializable
internal data class MessageWireItem(
    @SerialName("id") val id: String? = null,
    @SerialName("sender_id") val senderId: String? = null,
    @SerialName("msg_type") val msgType: String? = null,
    @SerialName("content") val content: String? = null,
    @SerialName("metadata") val metadata: MessageMetadataWire? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("sender_name") val senderName: String? = null,
    @SerialName("sender_image") val senderImage: String? = null,
    @SerialName("is_me") val isMe: Boolean? = null,
)

@Serializable
internal data class MessageMetadataWire(
    @SerialName("asset_id") val assetId: String? = null,
    @SerialName("asset_type") val assetType: String? = null,
    @SerialName("item_name") val itemName: String? = null,
    @SerialName("is_deleted") val isDeleted: Boolean? = null,
    @SerialName("share_at_display") val shareAtDisplay: String? = null,
    @SerialName("snapshot_title") val snapshotTitle: String? = null,
)

@Serializable
internal data class FriendProfileWireResponse(
    @SerialName("data") val data: FriendProfileWireData? = null,
)

@Serializable
internal data class FriendProfileWireData(
    @SerialName("user_info") val userInfo: FriendUserWireInfo? = null,
    @SerialName("item_preview") val itemPreview: List<ItemPreviewWire>? = null,
)

@Serializable
internal data class FriendUserWireInfo(
    @SerialName("id") val id: String? = null,
    @SerialName("username") val username: String? = null,
    @SerialName("email") val email: String? = null,
    @SerialName("first_name") val firstName: String? = null,
    @SerialName("last_name") val lastName: String? = null,
    @SerialName("phone_number") val phoneNumber: String? = null,
    @SerialName("profile") val profile: String? = null,
    @SerialName("birthday") val birthday: String? = null,
    @SerialName("shared_age") val sharedAge: Int? = null,
    @SerialName("shared_enabled") val sharedEnabled: Boolean? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
    @SerialName("is_friend") val isFriend: Boolean? = null,
    @SerialName("is_close") val isClose: Boolean? = null,
)

@Serializable
internal data class ItemPreviewWire(
    @SerialName("item_id") val itemId: String? = null,
    @SerialName("type") val type: String? = null,
    @SerialName("asset_detail") val assetDetail: AssetDetailWire? = null,
)

@Serializable
internal data class AssetDetailWire(
    @SerialName("id") val id: String? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("bank_name") val bankName: String? = null,
    @SerialName("account_number") val accountNumber: String? = null,
    @SerialName("amount") val amount: Double? = null,
    @SerialName("company_name") val companyName: String? = null,
    @SerialName("pol_num") val polNum: String? = null,
    @SerialName("coverage_amount") val coverageAmount: Double? = null,
    @SerialName("exp_date_text") val expDateText: String? = null,
    @SerialName("creditor") val creditor: String? = null,
    @SerialName("principal") val principal: Double? = null,
    @SerialName("location_text") val locationText: String? = null,
    @SerialName("location") val location: String? = null,
    @SerialName("deed_num") val deedNum: String? = null,
    @SerialName("area") val area: Double? = null,
    @SerialName("symbol") val symbol: String? = null,
    @SerialName("type_name") val typeName: String? = null,
    @SerialName("type") val type: String? = null,
)

@Serializable
internal data class PendingFriendWireResponse(
    @SerialName("data") val data: PendingFriendWireDataContainer? = null,
    @SerialName("error") val error: String? = null,
)

@Serializable
internal data class PendingFriendWireDataContainer(
    @SerialName("friends") val friends: List<PendingFriendWireData>? = null,
)

@Serializable
internal data class PendingFriendWireData(
    @SerialName("id") val id: String? = null,
    @SerialName("username") val username: String? = null,
    @SerialName("email") val email: String? = null,
    @SerialName("first_name") val firstName: String? = null,
    @SerialName("last_name") val lastName: String? = null,
    @SerialName("phone_number") val phoneNumber: String? = null,
    @SerialName("profile") val profile: String? = null,
    @SerialName("birthday") val birthday: String? = null,
    @SerialName("shared_age") val sharedAge: Int? = null,
    @SerialName("shared_enabled") val sharedEnabled: Boolean? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
    @SerialName("is_friend") val isFriend: Boolean? = null,
    @SerialName("is_close") val isClose: Boolean? = null,
)

private fun SearchFriendWireData.toDomain() = FriendData(
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

private fun FriendProfileWireData.toDomain() = FriendProfile(
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

private fun PendingFriendWireData.toDomain() = PendingFriend(
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
