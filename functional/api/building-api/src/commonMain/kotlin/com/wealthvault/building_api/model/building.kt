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
    @SerialName("id") val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("type") val type: String,
    @SerialName("name") val name: String,
    @SerialName("area") val area: Double,
    @SerialName("amount") val amount: Double,
    @SerialName("description") val description: String,
    @SerialName("location") val location: LocationData? = null,
    @SerialName("ins") val ins: List<InsData>? = emptyList(),
    @SerialName("reference_ids") val referenceIds: List<String>? = emptyList(),
    @SerialName("files") val files: List<FileData>? = emptyList(),
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)

@Serializable
data class LocationData(
    @SerialName("location_id") val locationId: String = "",
    @SerialName("address") val address: String = "",
    @SerialName("sub_district") val subDistrict: String = "",
    @SerialName("district") val district: String = "",
    @SerialName("province") val province: String = "",
    @SerialName("postal_code") val postalCode: String = "",
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
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


@Serializable
data class FileData(
    @SerialName("id") val id: String = "",
    @SerialName("url") val url: String = "",
    @SerialName("file_type") val fileType: String = ""
)