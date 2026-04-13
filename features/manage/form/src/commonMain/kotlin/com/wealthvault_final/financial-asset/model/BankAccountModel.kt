package com.wealthvault_final.`financial-asset`.model

import com.wealthvault_final.`financial-asset`.Imagepicker.Attachment
import kotlinx.serialization.Serializable

@Serializable
data class BankAccountModel(
    val type: String,
    val name: String,
    val amount: Double,
    val bankName: String,
    val bankId: String,
    val description: String,
    val attachments: List<Attachment>
)
