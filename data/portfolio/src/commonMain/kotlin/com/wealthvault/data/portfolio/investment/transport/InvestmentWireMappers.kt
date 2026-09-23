package com.wealthvault.data.portfolio.investment.transport

import com.wealthvault.core.model.FixedDecimal
import com.wealthvault.core.model.Money
import com.wealthvault.domain.portfolio.AssetFile
import com.wealthvault.domain.portfolio.GetInvestmentData
import com.wealthvault.domain.portfolio.InvestmentData
import com.wealthvault.domain.portfolio.InvestmentIdData
import com.wealthvault.domain.portfolio.InvestmentRequest
import com.wealthvault.data.portfolio.investment.transport.model.DeleteInvestmentResponse
import com.wealthvault.data.portfolio.investment.transport.model.FileUploadData as InvestmentWireFile
import com.wealthvault.data.portfolio.investment.transport.model.GetInvestmentData as GetInvestmentWireData
import com.wealthvault.data.portfolio.investment.transport.model.GetInvestmentResponse
import com.wealthvault.data.portfolio.investment.transport.model.InvestmentData as InvestmentWireData
import com.wealthvault.data.portfolio.investment.transport.model.InvestmentIdData as InvestmentWireIdData
import com.wealthvault.data.portfolio.investment.transport.model.InvestmentIdResponse
import com.wealthvault.data.portfolio.investment.transport.model.InvestmentRequest as InvestmentWireRequest
import com.wealthvault.data.portfolio.investment.transport.model.InvestmentResponse

/**
 * Legacy investment JSON is translated here. API consumers only see the
 * fixed-point portfolio contracts from the domain module.
 */
internal fun InvestmentRequest.toWire(): InvestmentWireRequest = InvestmentWireRequest(
    name = name,
    symbol = symbol,
    type = type,
    brokerName = brokerName,
    quantity = quantity?.decimalString(),
    costPerPrice = costPerPrice?.decimalString(),
    description = description,
    files = files.map { file ->
        InvestmentWireFile(
            bytes = file.bytes,
            mimeType = file.mimeType,
            fileName = file.fileName,
        )
    },
    deleteListId = deleteListId,
)

internal fun InvestmentResponse.requireDomainData(): InvestmentData {
    error?.let { throw IllegalStateException(it) }
    return data?.toDomain() ?: throw IllegalStateException("Investment response did not contain data")
}

internal fun GetInvestmentResponse.requireDomainData(): List<GetInvestmentData> {
    error?.let { throw IllegalStateException(it) }
    return data.orEmpty().map(GetInvestmentWireData::toDomain)
}

internal fun InvestmentIdResponse.toDomainData(): InvestmentIdData? {
    error?.let { throw IllegalStateException(it) }
    return data?.toDomain()
}

internal fun DeleteInvestmentResponse.requireSuccess() {
    error?.let { throw IllegalStateException(it) }
}

private fun InvestmentWireData.toDomain() = InvestmentData(
    id = id,
    userId = userId,
    name = name,
    symbol = symbol,
    type = type,
    brokerName = brokerName,
    quantity = FixedDecimal.fromDecimal(quantity?.toString(), scale = 4),
    costPerPrice = Money.fromDouble(costPerPrice),
    description = description,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

private fun GetInvestmentWireData.toDomain() = GetInvestmentData(
    id = id,
    userId = userId,
    name = name,
    symbol = symbol,
    type = type,
    brokerName = brokerName,
    quantity = FixedDecimal.fromDecimal(quantity?.toString(), scale = 4),
    costPerPrice = Money.fromDouble(costPerPrice),
    amount = Money.fromDouble(amount),
    description = description,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

private fun InvestmentWireIdData.toDomain() = InvestmentIdData(
    id = id,
    userId = userId,
    name = name,
    symbol = symbol,
    type = type,
    brokerName = brokerName,
    quantity = FixedDecimal.fromDecimal(quantity?.toString(), scale = 4),
    costPerPrice = Money.fromDouble(costPerPrice),
    amount = Money.fromDouble(amount),
    description = description,
    files = files?.map { file -> AssetFile(id = file.id, url = file.url, fileType = file.fileType) },
    createdAt = createdAt,
    updatedAt = updatedAt,
)
