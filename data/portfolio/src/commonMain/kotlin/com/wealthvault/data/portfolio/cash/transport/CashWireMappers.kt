package com.wealthvault.data.portfolio.cash.transport

import com.wealthvault.data.portfolio.cash.transport.model.CashData as CashWireData
import com.wealthvault.data.portfolio.cash.transport.model.CashFileUploadData as CashWireFileUploadData
import com.wealthvault.data.portfolio.cash.transport.model.CashIdData as CashWireIdData
import com.wealthvault.data.portfolio.cash.transport.model.CashIdResponse
import com.wealthvault.data.portfolio.cash.transport.model.CashRequest as CashWireRequest
import com.wealthvault.data.portfolio.cash.transport.model.CashResponse
import com.wealthvault.data.portfolio.cash.transport.model.DeleteCashResponse
import com.wealthvault.data.portfolio.cash.transport.model.GetCashData as GetCashWireData
import com.wealthvault.data.portfolio.cash.transport.model.GetCashResponse
import com.wealthvault.domain.portfolio.AssetFile
import com.wealthvault.domain.portfolio.CashData
import com.wealthvault.domain.portfolio.CashIdData
import com.wealthvault.domain.portfolio.CashRequest
import com.wealthvault.domain.portfolio.GetCashData

/**
 * Translation stays inside the API adapter. No feature or repository needs to
 * know the legacy JSON field names (including the historical `ammount` typo).
 */
internal fun CashRequest.toWire(): CashWireRequest = CashWireRequest(
    name = name,
    amount = amount?.toMajorUnits(),
    description = description,
    files = files?.map { file ->
        CashWireFileUploadData(
            bytes = file.bytes,
            mimeType = file.mimeType,
            fileName = file.fileName,
        )
    },
    deleteListId = deleteListId,
)

internal fun CashResponse.requireDomainData(): CashData {
    error?.let { throw IllegalStateException(it) }
    return data?.toDomain() ?: throw IllegalStateException("Cash response did not contain data")
}

internal fun GetCashResponse.requireDomainData(): List<GetCashData> {
    error?.let { throw IllegalStateException(it) }
    return data.map(GetCashWireData::toDomain)
}

internal fun CashIdResponse.toDomainData(): CashIdData? {
    error?.let { throw IllegalStateException(it) }
    return data?.toDomain()
}

internal fun DeleteCashResponse.requireSuccess() {
    error?.let { throw IllegalStateException(it) }
}

private fun CashWireData.toDomain() = CashData(
    id = id,
    userId = userId,
    name = name,
    amount = MoneyValue.fromDouble(ammount),
    description = description,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

private fun GetCashWireData.toDomain() = GetCashData(
    id = id,
    userId = userId,
    name = name,
    ammount = MoneyValue.fromDouble(ammount),
    description = description,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

private fun CashWireIdData.toDomain() = CashIdData(
    id = id,
    userId = userId,
    name = name,
    amount = MoneyValue.fromDouble(amount),
    description = description,
    files = files?.map { file ->
        AssetFile(id = file.id, url = file.url, fileType = file.fileType)
    },
    createdAt = createdAt,
    updatedAt = updatedAt,
)

/* Alias keeps this adapter independent of any wire-model name collision. */
private typealias MoneyValue = com.wealthvault.core.model.Money
