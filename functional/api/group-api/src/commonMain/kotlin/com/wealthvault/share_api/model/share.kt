package com.wealthvault.share_api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
internal data class ShareItemResponse(
    @SerialName("status")
    val status: String? = null,

    @SerialName("data")
    val data: Boolean? = null,

    @SerialName("error")
    val error: String? = null
)

@Serializable
internal data class ShareItemRequest(
    @SerialName("item_ids")
    val itemIds: String? = null,

    @SerialName("item_types")
    val itemTypes: String? = null,

    @SerialName("emails")
    val emails: List<TargetItem>? = null,

    @SerialName("friends")
    val friends: List<TargetItem>? = null,

    @SerialName("groups")
    val groups: List<TargetItem>? = null
)

@Serializable
internal data class TargetItem(
    @SerialName("id")
    val id: String? = null,

    @SerialName("share_at")
    val shareAt: String? = null
)


