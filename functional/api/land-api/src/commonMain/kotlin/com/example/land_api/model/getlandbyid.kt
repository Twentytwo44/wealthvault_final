package com.example.land_api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable




@Serializable
data class LandIdResponse(
    @SerialName("status")
    val status: String? = null,

    @SerialName("data")
    val data: LandIdData? = null,

    @SerialName("error")
    val error: String? = null
)

@Serializable
data class LandIdData(
    @SerialName("id")
    val id: String,

    @SerialName("user_id")
    val userId: String,

    @SerialName("name")
    val name: String,

    @SerialName("deed_num")
    val deedNum: String,

    @SerialName("area")
    val area: Int,

    @SerialName("amount")
    val amount: Int,

    @SerialName("description")
    val description: String,

    @SerialName("location")
    val location: LocationById,

    @SerialName("created_at")
    val createdAt: String,

    @SerialName("updated_at")
    val updatedAt: String,


    )

@Serializable
data class LocationById(
    @SerialName("location_id")
    val locationId: String,

    @SerialName("address")
    val address: String,

    @SerialName("sub_district")
    val subDistrict: String,

    @SerialName("district")
    val district: String,

    @SerialName("province")
    val province: String,

    @SerialName("postal_code")
    val postalCode: String,

    @SerialName("created_at")
    val createdAt: String,

    @SerialName("updated_at")
    val updatedAt: String,

    )







