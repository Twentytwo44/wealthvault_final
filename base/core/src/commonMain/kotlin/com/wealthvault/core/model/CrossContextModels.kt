package com.wealthvault.core.model

/**
 * Small read-model contracts shared by bounded contexts.
 *
 * These types are deliberately transport-free.  Profile/social/portfolio data
 * adapters map their endpoint DTOs into them, while the domain modules keep
 * their compatibility typealiases for existing consumers.
 */
data class FriendData(
    val id: String? = null,
    val username: String? = null,
    val email: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val phoneNumber: String? = null,
    val profile: String? = null,
    val birthday: String? = null,
    val sharedAge: Int? = null,
    val shareEnabled: Boolean? = null,
    val createdAt: String? = null,
    val isFriend: Boolean? = null,
    val updatedAt: String? = null,
)

data class AssetFile(
    val id: String = "",
    val url: String = "",
    val fileType: String = "",
)

data class BankAccountData(
    val id: String,
    val userId: String,
    val name: String? = null,
    val bankName: String? = null,
    val bankAccount: String? = null,
    val type: String? = null,
    val amount: Money? = null,
    val description: String? = null,
    val files: List<AssetFile>? = emptyList(),
    val createdAt: String? = null,
    val updatedAt: String? = null,
)

data class BuildingIdData(
    val id: String? = null,
    val userId: String? = null,
    val type: String? = null,
    val name: String? = null,
    val area: Double? = null,
    val amount: Money? = null,
    val description: String? = null,
    val location: BuildingLocation? = null,
    val ins: List<BuildingInsurance>? = emptyList(),
    val referenceIds: List<BuildingReference>? = emptyList(),
    val files: List<AssetFile>? = emptyList(),
    val createdAt: String? = null,
    val updatedAt: String? = null,
)

data class BuildingLocation(
    val locationId: String = "",
    val address: String = "",
    val subDistrict: String = "",
    val district: String = "",
    val province: String = "",
    val postalCode: String = "",
    val createdAt: String? = null,
    val updatedAt: String? = null,
)

data class BuildingInsurance(
    val insId: String = "",
    val insName: String = "",
)

data class BuildingReference(
    val refId: String = "",
    val refName: String = "",
)

data class CashIdData(
    val id: String,
    val userId: String,
    val name: String? = null,
    val amount: Money? = null,
    val description: String? = null,
    val files: List<AssetFile>? = emptyList(),
    val createdAt: String? = null,
    val updatedAt: String? = null,
)

data class InsuranceIdData(
    val id: String,
    val userId: String,
    val name: String? = null,
    val policyNumber: String? = null,
    val type: String? = null,
    val companyName: String? = null,
    val coveragePeriod: Int? = null,
    val coverageAmount: Money? = null,
    val conDate: String? = null,
    val expDate: String? = null,
    val description: String? = null,
    val files: List<AssetFile>? = emptyList(),
    val createdAt: String? = null,
    val updatedAt: String? = null,
)

data class InvestmentIdData(
    val id: String,
    val userId: String,
    val name: String? = null,
    val symbol: String? = null,
    val type: String? = null,
    val brokerName: String? = null,
    val quantity: FixedDecimal? = null,
    val costPerPrice: Money? = null,
    val amount: Money? = null,
    val description: String? = null,
    val files: List<AssetFile>? = emptyList(),
    val createdAt: String? = null,
    val updatedAt: String? = null,
)

data class LandIdData(
    val id: String,
    val userId: String,
    val name: String? = null,
    val deedNum: String? = null,
    val area: Double? = null,
    val amount: Money? = null,
    val description: String? = null,
    val location: LandLocation? = null,
    val files: List<AssetFile>? = emptyList(),
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val ref: List<LandReference>? = emptyList(),
)

data class LandLocation(
    val locationId: String = "",
    val address: String = "",
    val subDistrict: String = "",
    val district: String = "",
    val province: String = "",
    val postalCode: String = "",
    val createdAt: String? = null,
    val updatedAt: String? = null,
)

data class LandReference(
    val refId: String,
    val refName: String,
)

data class LiabilityIdData(
    val id: String,
    val userId: String,
    val type: String? = null,
    val name: String? = null,
    val creditor: String? = null,
    val principal: Money? = null,
    val interestRate: FixedDecimal? = null,
    val description: String? = null,
    val startedAt: String? = null,
    val endedAt: String? = null,
    val files: List<AssetFile>? = emptyList(),
    val createdAt: String? = null,
    val updatedAt: String? = null,
)
