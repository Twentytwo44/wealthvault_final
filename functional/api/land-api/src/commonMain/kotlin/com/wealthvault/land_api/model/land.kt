package com.wealthvault.land_api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class LandRequest(


    @SerialName("name")
    val name: String? = null,

    @SerialName("deed_num")
    val deedNum: String? = null,

    @SerialName("area")
    val area: Double? = null,

    @SerialName("amount")
    val amount: Double? = null,

    @SerialName("description")
    val description: String? = null,

    @SerialName("location.address")
    val locationAddress: String? = null,

    @SerialName("location.sub_district")
    val locationSubDistrict: String? = null,

    @SerialName("location.district")
    val locationDistrict: String? = null,

    @SerialName("location.province")
    val locationProvince: String? = null,

    @SerialName("location.postal_code")
    val locationPostalCode: String? = null,

    @SerialName("files")
    val files: List<LandFileUploadData> = emptyList(),

    @SerialName("reference_ids")
    val referenceIds: List <LandReferenceData> = emptyList(),


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
    val id: String? = null,

    @SerialName("user_id")
    val userId: String? = null,

    @SerialName("name")
    val name: String? = null,

    @SerialName("deed_num")
    val deedNum: String? = null,

    @SerialName("area")
    val area: Int? = null,

    @SerialName("amount")
    val amount: Int? = null,

    @SerialName("description")
    val description: String? = null,

    @SerialName("location")
    val location: Location? = null,

    @SerialName("created_at")
    val createdAt: String? = null,

    @SerialName("updated_at")
    val updatedAt: String? = null,


)

@Serializable
data class Location(
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
data class LandFileUploadData(
    val bytes: ByteArray? = null,
    val mimeType: String? = null,
    val fileName: String? = null,
)


@Serializable
data class  LandReferenceData(
    val areaName: String? = null,
    val areaId: String? = null,
)




