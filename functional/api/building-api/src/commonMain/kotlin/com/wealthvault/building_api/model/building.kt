package com.wealthvault.building_api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class BuildingRequest(
    @SerialName("type")
    val type: String? = null,

    @SerialName("name")
    val name: String? = null,

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

    @SerialName("ins_ids")
    val insIds: List<InsReferenceData> = emptyList(),

    @SerialName("files")
    val files: List<BuildingFileUploadData> = emptyList(),

    @SerialName("reference_ids")
    val referenceIds:  List<BuildingReferenceData> = emptyList(),


    )

@Serializable
data class BuildingResponse(
    @SerialName("status")
    val status: String?  = null,

    @SerialName("data")
    val data: BuildingData? = null,

    @SerialName("error")
    val error: String?  = null
)

@Serializable
data class BuildingData(

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
    val location: LocationData? = null,

    @SerialName("ins")
    val ins: List<InsData> = emptyList(),

    @SerialName("created_at")
    val createdAt: String? = null,

    @SerialName("updated_at")
    val updatedAt: String? = null,


)

@Serializable
data class LocationData(
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
data class InsData(
    @SerialName("ins_id")
    val insId: String? = null,

    @SerialName("ins_name")
    val insName: String? = null,

)


@Serializable
data class BuildingFileUploadData(
    val bytes: ByteArray,
    val mimeType: String? = null,
    val fileName: String? = null
)

@Serializable
data class BuildingReferenceData(
    val areaName: String? = null,
    val areaId: String? = null
)



@Serializable
data class InsReferenceData(
    val insName: String? = null,
    val insId: String? = null
)
