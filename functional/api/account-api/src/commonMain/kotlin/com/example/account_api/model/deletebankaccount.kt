package com.example.account_api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable




@Serializable
data class DeleteAccountResponse(
    @SerialName("status")
    val status: String? = null,

    @SerialName("data")
    val data: DeleteAccountData? = null,

    @SerialName("error")
    val error: String? = null
)

@Serializable
data class DeleteAccountData(
    @SerialName("success")
    val success: String,



)


