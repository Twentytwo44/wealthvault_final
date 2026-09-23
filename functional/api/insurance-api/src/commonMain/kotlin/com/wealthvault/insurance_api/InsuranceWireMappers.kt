package com.wealthvault.insurance_api

import com.wealthvault.core.model.Money
import com.wealthvault.domain.portfolio.AssetFile
import com.wealthvault.domain.portfolio.GetInsuranceData
import com.wealthvault.domain.portfolio.InsuranceData
import com.wealthvault.domain.portfolio.InsuranceIdData
import com.wealthvault.domain.portfolio.InsuranceRequest
import com.wealthvault.insurance_api.model.DeleteInsuranceResponse
import com.wealthvault.insurance_api.model.GetInsuranceData as GetInsuranceWireData
import com.wealthvault.insurance_api.model.GetInsuranceResponse
import com.wealthvault.insurance_api.model.InsuranceData as InsuranceWireData
import com.wealthvault.insurance_api.model.InsuranceFileUploadData as InsuranceWireFile
import com.wealthvault.insurance_api.model.InsuranceIdData as InsuranceWireIdData
import com.wealthvault.insurance_api.model.InsuranceIdResponse
import com.wealthvault.insurance_api.model.InsuranceRequest as InsuranceWireRequest
import com.wealthvault.insurance_api.model.InsuranceResponse

internal fun InsuranceRequest.toWire(): InsuranceWireRequest = InsuranceWireRequest(
    name = name,
    policyNumber = policyNumber,
    type = type,
    companyName = companyName,
    coveragePeriod = coveragePeriod,
    coverageAmount = coverageAmount?.toMajorUnits(),
    conDate = conDate,
    expDate = expDate,
    description = description,
    files = files.map { file ->
        InsuranceWireFile(bytes = file.bytes, mimeType = file.mimeType, fileName = file.fileName)
    },
    deleteListId = deleteListId,
)

internal fun InsuranceResponse.requireDomainData(): InsuranceData {
    error?.let { throw IllegalStateException(it) }
    return data?.toDomain() ?: throw IllegalStateException("Insurance response did not contain data")
}

internal fun GetInsuranceResponse.requireDomainData(): List<GetInsuranceData> {
    error?.let { throw IllegalStateException(it) }
    return data.orEmpty().map(GetInsuranceWireData::toDomain)
}

internal fun InsuranceIdResponse.toDomainData(): InsuranceIdData? {
    error?.let { throw IllegalStateException(it) }
    return data?.toDomain()
}

internal fun DeleteInsuranceResponse.requireSuccess() {
    error?.let { throw IllegalStateException(it) }
}

private fun InsuranceWireData.toDomain() = InsuranceData(
    id = id,
    userId = userId,
    name = name,
    policyNumber = policyNumber,
    type = type,
    companyName = companyName,
    coveragePeriod = coveragePeriod,
    coverageAmount = Money.fromDouble(coverageAmount),
    conDate = conDate,
    expDate = expDate,
    description = description,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

private fun GetInsuranceWireData.toDomain() = GetInsuranceData(
    id = id,
    userId = userId,
    name = name,
    policyNumber = policyNumber,
    type = type,
    companyName = companyName,
    coveragePeriod = coveragePeriod,
    coverageAmount = Money.fromDouble(coverageAmount),
    conDate = conDate,
    expDate = expDate,
    description = description,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

private fun InsuranceWireIdData.toDomain() = InsuranceIdData(
    id = id,
    userId = userId,
    name = name,
    policyNumber = policyNumber,
    type = type,
    companyName = companyName,
    coveragePeriod = coveragePeriod,
    coverageAmount = Money.fromDouble(coverageAmount),
    conDate = conDate,
    expDate = expDate,
    description = description,
    files = files?.map { file -> AssetFile(id = file.id, url = file.url, fileType = file.fileType) },
    createdAt = createdAt,
    updatedAt = updatedAt,
)
