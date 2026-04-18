package com.wealthvault_final.`financial-asset`.model

import kotlinx.serialization.Serializable

@Serializable
data class ShareTo(
    val email: List<ShareInfo>,
    val friend: List<ShareInfo>,
    val group: List<ShareInfo>,
    val shareAt: String,
)

@Serializable
data class ShareInfo(
    val name: String? = null,
    val userId: String? = null,
    val date: String? = null,
    val typeData: String? = null
)
