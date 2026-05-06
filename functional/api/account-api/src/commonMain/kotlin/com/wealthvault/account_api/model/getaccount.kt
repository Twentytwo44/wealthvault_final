package com.wealthvault.account_api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable




@Serializable
data class AccountResponse(
    @SerialName("status")
    val status: String? = null,

    @SerialName("data")
    val data: List<AccountData>? = null,

    @SerialName("error")
    val error: String? = null
)

@Serializable
data class AccountData(
    @SerialName("id")
    val id: String,

    @SerialName("user_id")
    val userId: String,

    @SerialName("name")
    val name: String? = null,

    @SerialName("bank_name")
    val bankName: String? = null,

    @SerialName("bank_account")
    val bankAccount: String? = null,

    @SerialName("type")
    val type: String? = null,

    @SerialName("amount")
    val amount: Int? = null,

    @SerialName("description")
    val description: String? = null



)

