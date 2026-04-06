package com.wealthvault.group_api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class GetGroupResponse(
    @SerialName("status")
    val status: String? = null,

    @SerialName("data")
    val data: List<GetAllGroupData>? = null,

    @SerialName("error")
    val error: String? = null
)



@Serializable
data class GetAllGroupData(

    @SerialName("id")
    val id: String? = null,

    @SerialName("group_name")
    val groupName: String? = null,

    @SerialName("group_profile")
    val groupProfile: String? = null,

    @SerialName("created_by")
    val createdBy: String? = null,

    @SerialName("member_count")
    val memberCount: String? = null,

    @SerialName("created_at")
    val createdAt: String? = null,

    @SerialName("updated_at")
    val updatedAt: String? = null,


    )

