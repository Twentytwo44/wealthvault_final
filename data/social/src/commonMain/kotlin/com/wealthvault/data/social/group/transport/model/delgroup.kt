package com.wealthvault.data.social.group.transport.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class DeleteGroupResponse(
    @SerialName("data")
    val data: Boolean? = null,

    @SerialName("message")
    val message: String? = null,

    @SerialName("error")
    val error: String? = null // เผื่อกรณี Backend ส่ง Error กลับมา
)
