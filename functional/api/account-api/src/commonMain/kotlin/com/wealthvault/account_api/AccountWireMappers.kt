package com.wealthvault.account_api

import com.wealthvault.account_api.model.AccountData as AccountWireData
import com.wealthvault.account_api.model.AccountResponse
import com.wealthvault.account_api.model.BankAccountData as BankAccountWireData
import com.wealthvault.account_api.model.BankAccountFileUploadData as BankAccountWireFile
import com.wealthvault.account_api.model.BankAccountRequest as BankAccountWireRequest
import com.wealthvault.account_api.model.BankAccountResponse
import com.wealthvault.account_api.model.DeleteAccountResponse
import com.wealthvault.domain.portfolio.AccountData
import com.wealthvault.domain.portfolio.AssetFile
import com.wealthvault.domain.portfolio.BankAccountData
import com.wealthvault.domain.portfolio.BankAccountRequest
import com.wealthvault.core.model.Money

/** Legacy account JSON is translated here and never crosses the API boundary. */
internal fun BankAccountRequest.toWire(): BankAccountWireRequest = BankAccountWireRequest(
    name = name,
    bankName = bankName,
    bankAccount = bankAccount,
    type = type,
    amount = amount.toMajorUnits(),
    description = description,
    files = files.map { file ->
        BankAccountWireFile(
            bytes = file.bytes,
            mimeType = file.mimeType,
            fileName = file.fileName,
        )
    },
    deleteListId = deleteListId,
)

internal fun BankAccountResponse.requireDomainData(): BankAccountData {
    error?.let { throw IllegalStateException(it) }
    return data?.toDomain() ?: throw IllegalStateException("Account response did not contain data")
}

internal fun AccountResponse.requireDomainData(): List<AccountData> {
    error?.let { throw IllegalStateException(it) }
    return data.orEmpty().map(AccountWireData::toDomain)
}

internal fun BankAccountResponse.toDomainData(): BankAccountData? {
    error?.let { throw IllegalStateException(it) }
    return data?.toDomain()
}

internal fun DeleteAccountResponse.requireSuccess() {
    error?.let { throw IllegalStateException(it) }
}

private fun AccountWireData.toDomain() = AccountData(
    id = id,
    userId = userId,
    name = name,
    bankName = bankName,
    bankAccount = bankAccount,
    type = type,
    amount = Money.fromDouble(amount),
    description = description,
)

private fun BankAccountWireData.toDomain() = BankAccountData(
    id = id,
    userId = userId,
    name = name,
    bankName = bankName,
    bankAccount = bankAccount,
    type = type,
    amount = Money.fromDouble(amount),
    description = description,
    files = files?.map { file -> AssetFile(id = file.id, url = file.url, fileType = file.fileType) },
    createdAt = createdAt,
    updatedAt = updatedAt,
)
