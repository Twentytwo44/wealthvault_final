package com.wealthvault_final.`financial-asset`.model

import com.wealthvault_final.`financial-asset`.Imagepicker.Attachment
import kotlinx.serialization.Serializable

@Serializable
data class CashModel(
    val cashName: String,
    val amount: Double,
    val description: String,
    val attachments: List<Attachment>

)
