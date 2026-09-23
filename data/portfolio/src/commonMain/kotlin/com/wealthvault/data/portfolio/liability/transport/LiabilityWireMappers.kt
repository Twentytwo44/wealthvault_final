package com.wealthvault.data.portfolio.liability.transport

import com.wealthvault.core.model.FixedDecimal
import com.wealthvault.core.model.Money
import com.wealthvault.domain.portfolio.AssetFile
import com.wealthvault.domain.portfolio.GetLiabilityData
import com.wealthvault.domain.portfolio.LiabilityData
import com.wealthvault.domain.portfolio.LiabilityIdData
import com.wealthvault.domain.portfolio.LiabilityRequest
import com.wealthvault.data.portfolio.liability.transport.model.DeleteLiabilityResponse
import com.wealthvault.data.portfolio.liability.transport.model.GetLiabilityData as GetLiabilityWireData
import com.wealthvault.data.portfolio.liability.transport.model.GetLiabilityResponse
import com.wealthvault.data.portfolio.liability.transport.model.LiabilityData as LiabilityWireData
import com.wealthvault.data.portfolio.liability.transport.model.LiabilityIdData as LiabilityWireIdData
import com.wealthvault.data.portfolio.liability.transport.model.LiabilityIdResponse
import com.wealthvault.data.portfolio.liability.transport.model.LiabilityRequest as LiabilityWireRequest
import com.wealthvault.data.portfolio.liability.transport.model.LiabilityResponse
import com.wealthvault.data.portfolio.liability.transport.model.LiabilityUploadData as LiabilityWireFile

internal fun LiabilityRequest.toWire(): LiabilityWireRequest = LiabilityWireRequest(
    type = type,
    name = name,
    creditor = creditor,
    principal = principal?.toMajorUnits(),
    interestRate = interestRate?.decimalString(),
    description = description,
    startedAt = startedAt,
    endedAt = endedAt,
    files = files.map { file -> LiabilityWireFile(file.bytes, file.mimeType, file.fileName) },
    deleteListId = deleteListId,
)

internal fun LiabilityResponse.requireDomainData(): LiabilityData {
    error?.let { throw IllegalStateException(it) }
    return data?.toDomain() ?: throw IllegalStateException("Liability response did not contain data")
}

internal fun GetLiabilityResponse.requireDomainData(): List<GetLiabilityData> {
    error?.let { throw IllegalStateException(it) }
    return data.orEmpty().map(GetLiabilityWireData::toDomain)
}

internal fun LiabilityIdResponse.toDomainData(): LiabilityIdData? {
    error?.let { throw IllegalStateException(it) }
    return data?.toDomain()
}

internal fun DeleteLiabilityResponse.requireSuccess() {
    error?.let { throw IllegalStateException(it) }
}

private fun LiabilityWireData.toDomain() = LiabilityData(
    id = id,
    userId = userId,
    type = type,
    name = name,
    creditor = creditor,
    principal = Money.fromDouble(principal),
    interestRate = FixedDecimal.fromDecimal(interestRate?.toString(), scale = 4),
    description = description,
    startedAt = startedAt,
    endedAt = endedAt,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

private fun GetLiabilityWireData.toDomain() = GetLiabilityData(
    id = id,
    userId = userId,
    type = type,
    name = name,
    creditor = creditor,
    principal = Money.fromDouble(principal),
    interestRate = FixedDecimal.fromDecimal(interestRate?.toString(), scale = 4),
    description = description,
    startedAt = startedAt,
    endedAt = endedAt,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

private fun LiabilityWireIdData.toDomain() = LiabilityIdData(
    id = id,
    userId = userId,
    type = type,
    name = name,
    creditor = creditor,
    principal = Money.fromDouble(principal),
    interestRate = FixedDecimal.fromDecimal(interestRate?.toString(), scale = 4),
    description = description,
    startedAt = startedAt,
    endedAt = endedAt,
    files = files?.map { file -> AssetFile(id = file.id, url = file.url, fileType = file.fileType) },
    createdAt = createdAt,
    updatedAt = updatedAt,
)
