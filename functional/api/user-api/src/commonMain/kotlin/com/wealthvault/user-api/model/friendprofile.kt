package com.wealthvault.`user-api`.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FriendProfileResponse(
    @SerialName("status") val status: String? = null,
    @SerialName("data") val data: FriendProfileData? = null
)

@Serializable
data class FriendProfileData(
    @SerialName("user_info") val userInfo: FriendUserInfo? = null,
    @SerialName("item_preview") val itemPreview: List<ItemPreview>? = null
)

@Serializable
data class FriendUserInfo(
    @SerialName("id") val id: String? = null,
    @SerialName("username") val username: String? = null,
    @SerialName("email") val email: String? = null,
    @SerialName("first_name") val firstName: String? = null,
    @SerialName("last_name") val lastName: String? = null,
    @SerialName("phone_number") val phoneNumber: String? = null,
    @SerialName("profile") val profile: String? = null,
    @SerialName("birthday") val birthday: String? = null,
    @SerialName("shared_age") val sharedAge: Int? = null,
    @SerialName("shared_enabled") val sharedEnabled: Boolean? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
    @SerialName("is_friend") val isFriend: Boolean? = null,
    @SerialName("is_close") val isClose: Boolean? = null
)

@Serializable
data class ItemPreview(
    @SerialName("item_id") val itemId: String? = null,
    @SerialName("type") val type: String? = null, // account, liability, insurance, cash, building, land, investment
    @SerialName("asset_detail") val assetDetail: AssetDetailPreview? = null
)

@Serializable
data class AssetDetailPreview(
    @SerialName("id") val id: String? = null,
    @SerialName("name") val name: String? = null,

    // --- 💰 บัญชีเงินฝาก / เงินสด ---
    @SerialName("bank_name") val bankName: String? = null,
    @SerialName("account_number") val accountNumber: String? = null,
    @SerialName("amount") val amount: Double? = null,

    // --- 🛡️ ประกัน ---
    @SerialName("company_name") val companyName: String? = null,
    @SerialName("pol_num") val polNum: String? = null,
    @SerialName("coverage_amount") val coverageAmount: Double? = null,
    @SerialName("exp_date_text") val expDateText: String? = null,

    // --- 💸 หนี้สิน ---
    @SerialName("creditor") val creditor: String? = null,
    @SerialName("principal") val principal: Double? = null,

    // --- 🏢 ที่ดิน / อาคาร ---
    @SerialName("location_text") val locationText: String? = null,
    @SerialName("location") val location: String? = null,
    @SerialName("deed_num") val deedNum: String? = null,
    @SerialName("area") val area: Double? = null,

    // --- 📈 การลงทุน ---
    @SerialName("symbol") val symbol: String? = null,
    @SerialName("type_name") val typeName: String? = null, // รับพวก INVEST_TYPE_GOLD

    // --- 🏷️ ประเภทซับเซต (เช่น LIABILITY_TYPE_LOAN, BUILDING_TYPE_HOUSE, INSURANCE_TYPE_PROPERTY) ---
    @SerialName("type") val type: String? = null
)