package com.wealthvault_final.`financial-obligations`.model

import com.wealthvault_final.`financial-asset`.Imagepicker.Attachment
import kotlinx.serialization.Serializable

@Serializable
data class ExpenseModel(
    val type: String,
    val name: String,
    val principal: Double,
    val interestRate: String,
    val description: String,
    val startedAt: String,
    val endedAt: String,
    val creditor: String,
    val attachments: List<Attachment>

)
