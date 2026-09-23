package com.wealthvault.data.portfolio.account.transport.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
internal data class DeleteAccountResponse(
    @SerialName("status")
    val status: String? = null,

    @SerialName("data")
    val data: DeleteAccountData? = null,

    @SerialName("error")
    val error: String? = null
)

@Serializable
internal data class DeleteAccountData(
    @SerialName("success")
    val success: String? = null,


    )

