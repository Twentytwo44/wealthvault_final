package com.wealthvault_final.`financial-asset`.model

import com.wealthvault_final.`financial-asset`.Imagepicker.Attachment
import kotlinx.serialization.Serializable

@Serializable
data class LandModel(
    val deedNum: String,
    val landName: String,
    val area: Double,
    val amount: Double,
    val description: String,
    val attachments: List<Attachment>,
    val referenceIds: List<RefModel>,
    val locationAddress: String,
    val locationSubDistrict: String,
    val locationDistrict: String,
    val locationProvince: String,
    val locationPostalCode: String,


    )
