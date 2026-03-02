package com.example.land_api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class LandRequest(
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

    @SerialName("location.address")
    val locationAddress: String,

    @SerialName("location.sub_district")
    val locationSubDistrict: String,

    @SerialName("location.district")
    val locationDistrict: String,

    @SerialName("location.province")
    val locationProvince: String,

    @SerialName("location.postal_code")
    val locationPostalCode: String,


    )

@Serializable
data class LandResponse(
    @SerialName("status")
    val status: String? = null,

    @SerialName("data")
    val data: LandData? = null,

    @SerialName("error")
    val error: String? = null
)

@Serializable
data class LandData(
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
    val location: Location,

    @SerialName("created_at")
    val createdAt: String,

    @SerialName("updated_at")
    val updatedAt: String,


)

@Serializable
data class Location(
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








