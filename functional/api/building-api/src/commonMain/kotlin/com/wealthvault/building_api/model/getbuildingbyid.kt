package com.wealthvault.building_api.model

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
    val id: String? = null,

    @SerialName("user_id")
    val userId: String? = null,

    @SerialName("type")
    val type: String? = null,

    @SerialName("name")
    val name: String? = null,

    @SerialName("area")
    val area: Int? = null,

    @SerialName("amount")
    val amount: Int? = null,

    @SerialName("description")
    val description: String? = null,

    @SerialName("location")
    val location: LocationDataById? = null,

    @SerialName("ins")
    val ins: List<InsDataById> = emptyList(),

    @SerialName("created_at")
    val createdAt: String? = null,

    @SerialName("updated_at")
    val updatedAt: String? = null,


    )

@Serializable
data class LocationDataById(
    @SerialName("location_id")
    val locationId: String? = null,

    @SerialName("address")
    val address: String? = null,

    @SerialName("sub_district")
    val subDistrict: String? = null,

    @SerialName("district")
    val district: String? = null,

    @SerialName("province")
    val province: String? = null,

    @SerialName("postal_code")
    val postalCode: String? = null,

    @SerialName("created_at")
    val createdAt: String? = null,

    @SerialName("updated_at")
    val updatedAt: String? = null,

    )

@Serializable
data class InsDataById(
    @SerialName("ins_id")
    val insId: String? = null,

    @SerialName("ins_name")
    val insName: String? = null,
)







