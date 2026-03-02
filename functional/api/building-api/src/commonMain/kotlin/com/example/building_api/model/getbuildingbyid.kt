package com.example.building_api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable




@Serializable
data class BuildingIdResponse(
    @SerialName("status")
    val status: String? = null,

    @SerialName("data")
    val data: BuildingIdData? = null,

    @SerialName("error")
    val error: String? = null
)

@Serializable
data class BuildingIdData(
    @SerialName("id")
    val id: String,

    @SerialName("user_id")
    val userId: String,

    @SerialName("type")
    val type: String,

    @SerialName("name")
    val name: String,

    @SerialName("area")
    val area: Int,

    @SerialName("amount")
    val amount: Int,

    @SerialName("description")
    val description: String,

    @SerialName("location")
    val location: LocationDataById,

    @SerialName("ins")
    val ins: List<InsDataById>,

    @SerialName("created_at")
    val createdAt: String,

    @SerialName("updated_at")
    val updatedAt: String,


    )

@Serializable
data class LocationDataById(
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

@Serializable
data class InsDataById(
    @SerialName("ins_id")
    val insId: String,

    @SerialName("ins_name")
    val insName: String,
)







