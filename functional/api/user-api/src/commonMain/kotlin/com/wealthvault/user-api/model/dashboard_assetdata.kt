package com.wealthvault.`user-api`.model // 🌟 เปลี่ยนชื่อ Package ให้ตรงกับโปรเจกต์ของคุณ Champ ได้เลยครับ

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ==========================================
// 🌟 1. Shared Models (ใช้ร่วมกันหลาย Asset)
// ==========================================

@Serializable
data class FileData(
    @SerialName("id") val id: String = "",
    @SerialName("url") val url: String = "",
    @SerialName("file_type") val fileType: String = ""
)

@Serializable
data class LocationData(
    @SerialName("location_id") val locationId: String = "",
    @SerialName("address") val address: String = "",
    @SerialName("sub_district") val subDistrict: String = "",
    @SerialName("district") val district: String = "",
    @SerialName("province") val province: String = "",
    @SerialName("postal_code") val postalCode: String = "",
    @SerialName("created_at") val createdAt: String = "",
    @SerialName("updated_at") val updatedAt: String = ""
)

@Serializable
data class RefData(
    @SerialName("ref_id") val refId: String = "",
    @SerialName("ref_name") val refName: String = ""
)


// ==========================================
// 🌟 2. Account (บัญชีเงินฝาก)
// ==========================================

@Serializable
data class AccountDetailResponse(
    @SerialName("status") val status: String? = null,
    @SerialName("data") val data: AccountData? = null
)

@Serializable
data class AccountData(
    @SerialName("id") val id: String = "",
    @SerialName("user_id") val userId: String = "",
    @SerialName("name") val name: String = "",
    @SerialName("bank_name") val bankName: String = "",
    @SerialName("bank_account") val bankAccount: String = "",
    @SerialName("type") val type: String = "",
    @SerialName("amount") val amount: Double = 0.0,
    @SerialName("description") val description: String = "",
    @SerialName("files") val files: List<FileData>? = emptyList(),
    @SerialName("created_at") val createdAt: String = "",
    @SerialName("updated_at") val updatedAt: String = ""
)


// ==========================================
// 🌟 3. Cash (เงินสด ทองคำ)
// ==========================================

@Serializable
data class CashDetailResponse(
    @SerialName("status") val status: String? = null,
    @SerialName("data") val data: GetCashData? = null
)

@Serializable
data class GetCashData(
    @SerialName("id") val id: String = "",
    @SerialName("user_id") val userId: String = "",
    @SerialName("name") val name: String = "",
    @SerialName("amount") val amount: Double = 0.0,
    @SerialName("description") val description: String = "",
    @SerialName("files") val files: List<FileData>? = emptyList(),
    @SerialName("created_at") val createdAt: String = "",
    @SerialName("updated_at") val updatedAt: String = ""
)


// ==========================================
// 🌟 4. Investment (ลงทุน หุ้น กองทุน)
// ==========================================

@Serializable
data class InvestDetailResponse(
    @SerialName("status") val status: String? = null,
    @SerialName("data") val data: GetInvestmentData? = null
)

@Serializable
data class GetInvestmentData(
    @SerialName("id") val id: String = "",
    @SerialName("user_id") val userId: String = "",
    @SerialName("name") val name: String = "",
    @SerialName("symbol") val symbol: String = "",
    @SerialName("type") val type: String = "",
    @SerialName("broker_name") val brokerName: String = "",
    @SerialName("quantity") val quantity: Int = 0,
    @SerialName("cost_per_price") val costPerPrice: Double = 0.0,
    @SerialName("amount") val amount: Double = 0.0,
    @SerialName("description") val description: String = "",
    @SerialName("files") val files: List<FileData>? = emptyList(),
    @SerialName("created_at") val createdAt: String = "",
    @SerialName("updated_at") val updatedAt: String = ""
)


// ==========================================
// 🌟 5. Insurance (ประกัน)
// ==========================================

@Serializable
data class InsuranceDetailResponse(
    @SerialName("status") val status: String? = null,
    @SerialName("data") val data: GetInsuranceData? = null
)

@Serializable
data class GetInsuranceData(
    @SerialName("id") val id: String = "",
    @SerialName("user_id") val userId: String = "",
    @SerialName("name") val name: String = "",
    @SerialName("policy_number") val policyNumber: String = "",
    @SerialName("type") val type: String = "",
    @SerialName("company_name") val companyName: String = "",
    @SerialName("coverage_period") val coveragePeriod: Int = 0,
    @SerialName("coverage_amount") val coverageAmount: Double = 0.0,
    @SerialName("con_date") val conDate: String = "",
    @SerialName("exp_date") val expDate: String = "",
    @SerialName("description") val description: String = "",
    @SerialName("files") val files: List<FileData>? = emptyList(),
    @SerialName("created_at") val createdAt: String = "",
    @SerialName("updated_at") val updatedAt: String = ""
)


// ==========================================
// 🌟 6. Land (ที่ดิน)
// ==========================================

@Serializable
data class LandDetailResponse(
    @SerialName("status") val status: String? = null,
    @SerialName("data") val data: GetLandData? = null
)

@Serializable
data class GetLandData(
    @SerialName("id") val id: String = "",
    @SerialName("user_id") val userId: String = "",
    @SerialName("name") val name: String = "",
    @SerialName("deed_num") val deedNum: String = "",
    @SerialName("area") val area: Double = 0.0,
    @SerialName("amount") val amount: Double = 0.0,
    @SerialName("description") val description: String = "",
    @SerialName("location") val location: LocationData? = null,
    @SerialName("ref") val ref: List<RefData>? = emptyList(),
    @SerialName("files") val files: List<FileData>? = emptyList(),
    @SerialName("created_at") val createdAt: String = "",
    @SerialName("updated_at") val updatedAt: String = ""
)


// ==========================================
// 🌟 7. Building (บ้าน ตึก อาคาร)
// ==========================================
// หมายเหตุ: ตาม JSON ที่ส่งมา data เป็น Array [ { ... } ] เราเลยให้รับเป็น List
// ถ้า Backend ปรับให้ส่งเป็น Object ก้อนเดียว ให้ลบ List<> ออกนะครับ

@Serializable
data class BuildingDetailResponse(
    @SerialName("status") val status: String? = null,
    @SerialName("data") val data: List<GetBuildingData>? = emptyList() // 🌟 ตาม JSON เป็น List
)

@Serializable
data class GetBuildingData(
    @SerialName("id") val id: String = "",
    @SerialName("user_id") val userId: String = "",
    @SerialName("type") val type: String = "",
    @SerialName("name") val name: String = "",
    @SerialName("area") val area: Double = 0.0,
    @SerialName("amount") val amount: Double = 0.0,
    @SerialName("description") val description: String = "",
    @SerialName("location") val location: LocationData? = null,
    @SerialName("reference_ids") val referenceIds: List<String>? = emptyList(),
    @SerialName("ins_ids") val insIds: List<String>? = emptyList(),
    @SerialName("files") val files: List<FileData>? = emptyList(),
    @SerialName("created_at") val createdAt: String = "",
    @SerialName("updated_at") val updatedAt: String = ""
)


// ==========================================
// 🌟 8. Liability (หนี้สิน)
// ==========================================

@Serializable
data class LiabilityDetailResponse(
    @SerialName("status") val status: String? = null,
    @SerialName("data") val data: LiabilityData? = null
)

@Serializable
data class LiabilityData(
    @SerialName("id") val id: String = "",
    @SerialName("user_id") val userId: String = "",
    @SerialName("type") val type: String = "",
    @SerialName("name") val name: String = "",
    @SerialName("creditor") val creditor: String = "",
    @SerialName("principal") val principal: Double = 0.0,
    @SerialName("interest_rate") val interestRate: Double = 0.0,
    @SerialName("description") val description: String = "",
    @SerialName("started_at") val startedAt: String = "",
    @SerialName("ended_at") val endedAt: String = "",
    @SerialName("files") val files: List<FileData>? = emptyList(),
    @SerialName("created_at") val createdAt: String = "",
    @SerialName("updated_at") val updatedAt: String = ""
)