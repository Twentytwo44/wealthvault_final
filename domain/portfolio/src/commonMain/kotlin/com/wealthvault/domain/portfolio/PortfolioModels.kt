package com.wealthvault.domain.portfolio

import com.wealthvault.core.model.Attachment
import com.wealthvault.core.model.FixedDecimal
import com.wealthvault.core.model.Money

/**
 * Domain representations of portfolio data.  These types deliberately have
 * no serialization annotations or transport-specific names; API adapters map
 * them to/from the legacy endpoint DTOs at the data boundary.
 */

typealias AssetFile = com.wealthvault.core.model.AssetFile

typealias BankAccountData = com.wealthvault.core.model.BankAccountData

data class AccountData(
    val id: String,
    val userId: String,
    val name: String? = null,
    val bankName: String? = null,
    val bankAccount: String? = null,
    val type: String? = null,
    val amount: Money? = null,
    val description: String? = null,
)

typealias BuildingIdData = com.wealthvault.core.model.BuildingIdData
typealias BuildingLocation = com.wealthvault.core.model.BuildingLocation
typealias BuildingInsurance = com.wealthvault.core.model.BuildingInsurance
typealias BuildingReference = com.wealthvault.core.model.BuildingReference

data class GetBuildingData(
    val id: String? = null,
    val userId: String? = null,
    val type: String? = null,
    val name: String? = null,
    val area: Double? = null,
    val amount: Money? = null,
    val description: String? = null,
    val location: BuildingLocation? = null,
    val ins: List<BuildingInsurance>? = emptyList(),
    val referenceIds: List<String>? = emptyList(),
    val files: List<AssetFile>? = emptyList(),
    val createdAt: String? = null,
    val updatedAt: String? = null,
)

typealias CashIdData = com.wealthvault.core.model.CashIdData

data class CashData(
    val id: String? = null,
    val userId: String? = null,
    val name: String? = null,
    val amount: Money? = null,
    val description: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
)

data class GetCashData(
    val id: String? = null,
    val userId: String? = null,
    val name: String? = null,
    val ammount: Money? = null,
    val description: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
)

typealias InsuranceIdData = com.wealthvault.core.model.InsuranceIdData

data class GetInsuranceData(
    val id: String? = null,
    val userId: String? = null,
    val name: String? = null,
    val policyNumber: String? = null,
    val type: String? = null,
    val companyName: String? = null,
    val coveragePeriod: Int? = null,
    val coverageAmount: Money? = null,
    val conDate: String? = null,
    val expDate: String? = null,
    val description: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
)

typealias InvestmentIdData = com.wealthvault.core.model.InvestmentIdData

data class GetInvestmentData(
    val id: String? = null,
    val userId: String? = null,
    val name: String? = null,
    val symbol: String? = null,
    val type: String? = null,
    val brokerName: String? = null,
    val quantity: FixedDecimal? = null,
    val costPerPrice: Money? = null,
    val amount: Money? = null,
    val description: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
)

typealias LandIdData = com.wealthvault.core.model.LandIdData
typealias LandLocation = com.wealthvault.core.model.LandLocation
typealias LandReference = com.wealthvault.core.model.LandReference

data class GetLandData(
    val id: String? = null,
    val userId: String? = null,
    val name: String? = null,
    val deedNum: String? = null,
    val area: Int? = null,
    val amount: Money? = null,
    val description: String? = null,
    val location: LandLocation? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
)

typealias LiabilityIdData = com.wealthvault.core.model.LiabilityIdData

data class GetLiabilityData(
    val id: String? = null,
    val userId: String? = null,
    val type: String? = null,
    val name: String? = null,
    val creditor: String? = null,
    val principal: Money? = null,
    val interestRate: FixedDecimal? = null,
    val description: String? = null,
    val startedAt: String? = null,
    val endedAt: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
)

/**
 * Responses returned by create/update endpoints.  They intentionally live in
 * the domain module so presentation never needs to know which endpoint DTO
 * produced the newly-created asset.
 */
data class BuildingData(
    val id: String? = null,
    val userId: String? = null,
    val type: String? = null,
    val name: String? = null,
    val area: Double? = null,
    val amount: Money? = null,
    val description: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
)

data class InsuranceData(
    val id: String? = null,
    val userId: String? = null,
    val name: String? = null,
    val policyNumber: String? = null,
    val type: String? = null,
    val companyName: String? = null,
    val coveragePeriod: Int? = null,
    val coverageAmount: Money? = null,
    val conDate: String? = null,
    val expDate: String? = null,
    val description: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
)

data class InvestmentData(
    val id: String? = null,
    val userId: String? = null,
    val name: String? = null,
    val symbol: String? = null,
    val type: String? = null,
    val brokerName: String? = null,
    val quantity: FixedDecimal? = null,
    val costPerPrice: Money? = null,
    val description: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
)

data class LandData(
    val id: String? = null,
    val userId: String? = null,
    val name: String? = null,
    val deedNum: String? = null,
    val area: Int? = null,
    val amount: Money? = null,
    val description: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
)

data class LiabilityData(
    val id: String? = null,
    val userId: String? = null,
    val type: String? = null,
    val name: String? = null,
    val creditor: String? = null,
    val principal: Money? = null,
    val interestRate: FixedDecimal? = null,
    val description: String? = null,
    val startedAt: String? = null,
    val endedAt: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
)

data class BuildingReferenceData(val areaName: String? = null, val areaId: String? = null)
data class InsReferenceData(val insName: String? = null, val insId: String? = null)
data class LandReferenceData(val areaName: String? = null, val areaId: String? = null)

data class BankAccountFileUploadData(val bytes: ByteArray, val mimeType: String, val fileName: String)
data class BuildingFileUploadData(val bytes: ByteArray, val mimeType: String? = null, val fileName: String? = null)
data class CashFileUploadData(val bytes: ByteArray? = null, val mimeType: String? = null, val fileName: String? = null)
data class InsuranceFileUploadData(val bytes: ByteArray? = null, val mimeType: String? = null, val fileName: String? = null)
data class FileUploadData(val bytes: ByteArray? = null, val mimeType: String? = null, val fileName: String? = null)
data class LandFileUploadData(val bytes: ByteArray? = null, val mimeType: String? = null, val fileName: String? = null)
data class LiabilityUploadData(val bytes: ByteArray, val mimeType: String, val fileName: String)

data class BankAccountRequest(
    val name: String,
    val bankName: String,
    val bankAccount: String,
    val type: String,
    /** Monetary values stay fixed-point until the transport adapter. */
    val amount: Money,
    val description: String,
    val files: List<BankAccountFileUploadData> = emptyList(),
    val deleteListId: List<String>? = emptyList(),
)

data class BuildingRequest(
    val type: String? = null,
    val name: String? = null,
    val area: Double? = null,
    val amount: Money? = null,
    val description: String? = null,
    val locationAddress: String? = null,
    val locationSubDistrict: String? = null,
    val locationDistrict: String? = null,
    val locationProvince: String? = null,
    val locationPostalCode: String? = null,
    val insIds: List<InsReferenceData> = emptyList(),
    val files: List<BuildingFileUploadData> = emptyList(),
    val referenceIds: List<BuildingReferenceData> = emptyList(),
    val deleteListId: List<String> = emptyList(),
    val deleteRefListId: List<BuildingReferenceData> = emptyList(),
    val deleteInsListId: List<InsReferenceData> = emptyList(),
)

data class CashRequest(
    val name: String? = null,
    val amount: Money? = null,
    val description: String? = null,
    val files: List<CashFileUploadData>? = emptyList(),
    val deleteListId: List<String>? = emptyList(),
)

data class InsuranceRequest(
    val name: String? = null,
    val policyNumber: String? = null,
    val type: String? = null,
    val companyName: String? = null,
    val coveragePeriod: String? = null,
    val coverageAmount: Money? = null,
    val conDate: String? = null,
    val expDate: String? = null,
    val description: String? = null,
    val files: List<InsuranceFileUploadData> = emptyList(),
    val deleteListId: List<String> = emptyList(),
)

data class InvestmentRequest(
    val name: String? = null,
    val symbol: String? = null,
    val type: String? = null,
    val brokerName: String? = null,
    /** Quantity is fixed-scale so the command layer never parses a Double. */
    val quantity: FixedDecimal? = null,
    /** Cost is monetary and remains fixed-point until the transport adapter. */
    val costPerPrice: Money? = null,
    val description: String? = null,
    val files: List<FileUploadData> = emptyList(),
    val deleteListId: List<String> = emptyList(),
)

data class LandRequest(
    val name: String? = null,
    val deedNum: String? = null,
    val area: Double? = null,
    val amount: Money? = null,
    val description: String? = null,
    val locationAddress: String? = null,
    val locationSubDistrict: String? = null,
    val locationDistrict: String? = null,
    val locationProvince: String? = null,
    val locationPostalCode: String? = null,
    val files: List<LandFileUploadData> = emptyList(),
    val referenceIds: List<LandReferenceData> = emptyList(),
    val deleteListId: List<String> = emptyList(),
    val deleteRefListId: List<LandReferenceData> = emptyList(),
)

data class LiabilityRequest(
    val type: String? = null,
    val name: String? = null,
    val creditor: String? = null,
    val principal: Money? = null,
    /** Interest rates use four fixed decimal places at the domain boundary. */
    val interestRate: FixedDecimal? = null,
    val description: String? = null,
    val startedAt: String? = null,
    val endedAt: String? = null,
    val files: List<LiabilityUploadData> = emptyList(),
    val deleteListId: List<String> = emptyList(),
)

/** Presentation-only form values. They are domain-owned and do not expose API DTOs. */
data class BankAccountModel(
    val type: String,
    val name: String,
    val amount: Money,
    val bankName: String,
    val bankId: String,
    val description: String,
    val attachments: List<Attachment>,
)

data class BuildingModel(
    val type: String,
    val buildingName: String,
    val area: Double,
    val amount: Money,
    val description: String,
    val attachments: List<Attachment>,
    val referenceIds: List<RefModel>,
    val locationAddress: String,
    val locationSubDistrict: String,
    val locationDistrict: String,
    val locationProvince: String,
    val locationPostalCode: String,
    val insIds: List<InsRefModel>,
)

data class RefModel(
    val areaName: String,
    val areaId: String,
    val deedNum: String = "",
)

data class InsRefModel(
    val insName: String,
    val insId: String,
    val policyNum: String = "",
)

data class CashModel(
    val cashName: String,
    val amount: Money,
    val description: String,
    val attachments: List<Attachment>,
)

data class InsuranceModel(
    val policyNumber: String,
    val type: String,
    val companyName: String,
    val coverageAmount: Money,
    val coveragePeriod: String,
    val expDate: String,
    val description: String,
    val attachments: List<Attachment>,
    val conDate: String,
    val name: String,
)

data class StockModel(
    val stockName: String,
    val quantity: FixedDecimal,
    val description: String,
    val stockSymbol: String,
    val brokerName: String,
    val costPerPrice: Money,
    val attachments: List<Attachment>,
    val type: String,
)

data class LandModel(
    val deedNum: String,
    val landName: String,
    val area: Double,
    val amount: Money,
    val description: String,
    val attachments: List<Attachment>,
    val referenceIds: List<RefModel>,
    val locationAddress: String,
    val locationSubDistrict: String,
    val locationDistrict: String,
    val locationProvince: String,
    val locationPostalCode: String,
)
