package com.wealthvault.land_api

import com.wealthvault.core.model.Money
import com.wealthvault.domain.portfolio.AssetFile
import com.wealthvault.domain.portfolio.GetLandData
import com.wealthvault.domain.portfolio.LandData
import com.wealthvault.domain.portfolio.LandIdData
import com.wealthvault.domain.portfolio.LandLocation
import com.wealthvault.domain.portfolio.LandReference
import com.wealthvault.domain.portfolio.LandRequest
import com.wealthvault.land_api.model.DeleteLandResponse
import com.wealthvault.land_api.model.GetLandData as GetLandWireData
import com.wealthvault.land_api.model.GetLandResponse
import com.wealthvault.land_api.model.LandData as LandWireData
import com.wealthvault.land_api.model.LandFileUploadData as LandWireFile
import com.wealthvault.land_api.model.LandIdData as LandWireIdData
import com.wealthvault.land_api.model.LandIdResponse
import com.wealthvault.land_api.model.LandReferenceData as LandWireReference
import com.wealthvault.land_api.model.LandRequest as LandWireRequest
import com.wealthvault.land_api.model.LandResponse

internal fun LandRequest.toWire(): LandWireRequest = LandWireRequest(
    name = name,
    deedNum = deedNum,
    area = area,
    amount = amount?.toMajorUnits(),
    description = description,
    locationAddress = locationAddress,
    locationSubDistrict = locationSubDistrict,
    locationDistrict = locationDistrict,
    locationProvince = locationProvince,
    locationPostalCode = locationPostalCode,
    files = files.map { file -> LandWireFile(bytes = file.bytes, mimeType = file.mimeType, fileName = file.fileName) },
    referenceIds = referenceIds.map { ref -> LandWireReference(areaName = ref.areaName, areaId = ref.areaId) },
    deleteListId = deleteListId,
    deleteRefListId = deleteRefListId.map { ref -> LandWireReference(areaName = ref.areaName, areaId = ref.areaId) },
)

internal fun LandResponse.requireDomainData(): LandData {
    error?.let { throw IllegalStateException(it) }
    return data?.toDomain() ?: throw IllegalStateException("Land response did not contain data")
}

internal fun GetLandResponse.requireDomainData(): List<GetLandData> {
    error?.let { throw IllegalStateException(it) }
    return data.orEmpty().map(GetLandWireData::toDomain)
}

internal fun LandIdResponse.toDomainData(): LandIdData? {
    error?.let { throw IllegalStateException(it) }
    return data?.toDomain()
}

internal fun DeleteLandResponse.requireSuccess() {
    error?.let { throw IllegalStateException(it) }
}

private fun LandWireData.toDomain() = LandData(
    id = id,
    userId = userId,
    name = name,
    deedNum = deedNum,
    area = area,
    amount = Money.fromDouble(amount?.toDouble()),
    description = description,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

private fun GetLandWireData.toDomain() = GetLandData(
    id = id,
    userId = userId,
    name = name,
    deedNum = deedNum,
    area = area,
    amount = Money.fromDouble(amount?.toDouble()),
    description = description,
    location = location?.let { value ->
        LandLocation(
            locationId = value.locationId.orEmpty(),
            address = value.address.orEmpty(),
            subDistrict = value.subDistrict.orEmpty(),
            district = value.district.orEmpty(),
            province = value.province.orEmpty(),
            postalCode = value.postalCode.orEmpty(),
            createdAt = value.createdAt,
            updatedAt = value.updatedAt,
        )
    },
    createdAt = createdAt,
    updatedAt = updatedAt,
)

private fun LandWireIdData.toDomain() = LandIdData(
    id = id,
    userId = userId,
    name = name,
    deedNum = deedNum,
    area = area,
    amount = Money.fromDouble(amount),
    description = description,
    location = location?.let { value ->
        LandLocation(
            locationId = value.locationId,
            address = value.address,
            subDistrict = value.subDistrict,
            district = value.district,
            province = value.province,
            postalCode = value.postalCode,
            createdAt = value.createdAt,
            updatedAt = value.updatedAt,
        )
    },
    files = files?.map { file -> AssetFile(id = file.id, url = file.url, fileType = file.fileType) },
    createdAt = createdAt,
    updatedAt = updatedAt,
    ref = ref?.map { reference -> LandReference(refId = reference.refId, refName = reference.refName) },
)
