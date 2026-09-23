package com.wealthvault.data.portfolio.building.transport

import com.wealthvault.core.model.Money
import com.wealthvault.domain.portfolio.AssetFile
import com.wealthvault.domain.portfolio.BuildingData
import com.wealthvault.domain.portfolio.BuildingIdData
import com.wealthvault.domain.portfolio.BuildingInsurance
import com.wealthvault.domain.portfolio.BuildingLocation
import com.wealthvault.domain.portfolio.BuildingReference
import com.wealthvault.domain.portfolio.BuildingRequest
import com.wealthvault.domain.portfolio.GetBuildingData
import com.wealthvault.data.portfolio.building.transport.model.BuildingData as BuildingWireData
import com.wealthvault.data.portfolio.building.transport.model.BuildingFileUploadData as BuildingWireFile
import com.wealthvault.data.portfolio.building.transport.model.BuildingIdData as BuildingWireIdData
import com.wealthvault.data.portfolio.building.transport.model.BuildingReferenceData as BuildingWireReference
import com.wealthvault.data.portfolio.building.transport.model.BuildingRequest as BuildingWireRequest
import com.wealthvault.data.portfolio.building.transport.model.DeleteBuildingResponse
import com.wealthvault.data.portfolio.building.transport.model.GetBuildingData as GetBuildingWireData
import com.wealthvault.data.portfolio.building.transport.model.GetBuildingResponse
import com.wealthvault.data.portfolio.building.transport.model.InsReferenceData as InsWireReference
import com.wealthvault.data.portfolio.building.transport.model.BuildingResponse
import com.wealthvault.data.portfolio.building.transport.model.BuildingIdResponse

internal fun BuildingRequest.toWire(): BuildingWireRequest = BuildingWireRequest(
    type = type,
    name = name,
    area = area,
    amount = amount?.toMajorUnits(),
    description = description,
    locationAddress = locationAddress,
    locationSubDistrict = locationSubDistrict,
    locationDistrict = locationDistrict,
    locationProvince = locationProvince,
    locationPostalCode = locationPostalCode,
    insIds = insIds.map { InsWireReference(insName = it.insName, insId = it.insId) },
    files = files.map { file -> BuildingWireFile(bytes = file.bytes, mimeType = file.mimeType, fileName = file.fileName) },
    referenceIds = referenceIds.map { ref -> BuildingWireReference(areaName = ref.areaName, areaId = ref.areaId) },
    deleteListId = deleteListId,
    deleteRefListId = deleteRefListId.map { ref -> BuildingWireReference(areaName = ref.areaName, areaId = ref.areaId) },
    deleteInsListId = deleteInsListId.map { ref -> InsWireReference(insName = ref.insName, insId = ref.insId) },
)

internal fun BuildingResponse.requireDomainData(): BuildingData {
    error?.let { throw IllegalStateException(it) }
    return data?.toDomain() ?: throw IllegalStateException("Building response did not contain data")
}

internal fun GetBuildingResponse.requireDomainData(): List<GetBuildingData> {
    error?.let { throw IllegalStateException(it) }
    return data.orEmpty().map(GetBuildingWireData::toDomain)
}

internal fun BuildingIdResponse.toDomainData(): BuildingIdData? {
    error?.let { throw IllegalStateException(it) }
    return data?.toDomain()
}

internal fun DeleteBuildingResponse.requireSuccess() {
    error?.let { throw IllegalStateException(it) }
}

private fun BuildingWireData.toDomain() = BuildingData(
    id = id,
    userId = userId,
    type = type,
    name = name,
    area = area,
    amount = Money.fromDouble(amount),
    description = description,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

private fun GetBuildingWireData.toDomain() = GetBuildingData(
    id = id,
    userId = userId,
    type = type,
    name = name,
    area = area,
    amount = Money.fromDouble(amount),
    description = description,
    location = location?.let { value ->
        BuildingLocation(
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
    ins = ins?.map { value -> BuildingInsurance(insId = value.insId, insName = value.insName) },
    referenceIds = referenceIds,
    files = files?.map { value -> AssetFile(id = value.id, url = value.url, fileType = value.fileType) },
    createdAt = createdAt,
    updatedAt = updatedAt,
)

private fun BuildingWireIdData.toDomain() = BuildingIdData(
    id = id,
    userId = userId,
    type = type,
    name = name,
    area = area,
    amount = Money.fromDouble(amount),
    description = description,
    location = location?.let { value ->
        BuildingLocation(
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
    ins = ins?.map { value -> BuildingInsurance(insId = value.insId, insName = value.insName) },
    referenceIds = referenceIds?.map { value -> BuildingReference(refId = value.refId, refName = value.refName) },
    files = files?.map { value -> AssetFile(id = value.id, url = value.url, fileType = value.fileType) },
    createdAt = createdAt,
    updatedAt = updatedAt,
)
