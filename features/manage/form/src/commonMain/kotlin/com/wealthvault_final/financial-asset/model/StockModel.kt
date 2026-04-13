package com.wealthvault_final.`financial-asset`.model

import com.wealthvault_final.`financial-asset`.Imagepicker.Attachment
import kotlinx.serialization.Serializable

@Serializable
data class StockModel(
    val stockName: String,
    val quantity: Double,
    val description: String,
    val stockSymbol: String,
    val brokerName: String,
    val costPerPrice: Double,
    val attachments: List<Attachment>

)
