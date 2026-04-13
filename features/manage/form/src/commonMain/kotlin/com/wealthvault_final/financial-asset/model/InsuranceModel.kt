package com.wealthvault_final.`financial-asset`.model

import com.wealthvault_final.`financial-asset`.Imagepicker.Attachment
import kotlinx.serialization.Serializable

@Serializable
data class InsuranceModel (

    val policyNumber: String,
    val type: String,
    val companyName: String,
    val coverageAmount: Double,
    val coveragePeroid: String,
    val expDate: String,
    val description: String,
    val attachments: List<Attachment>,
    val conDate: String,
    val name: String,

    )
