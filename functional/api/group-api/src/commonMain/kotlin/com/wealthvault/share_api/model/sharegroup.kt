package com.wealthvault.share_api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ShareGroupResponse(
    @SerialName("status")
    val status: String? = null,

    @SerialName("data")
    val data: List<ShareGroupData>? = null,

    @SerialName("error")
    val error: String? = null
)

@Serializable
data class ShareGroupData(
    @SerialName("group_item_id")
    val groupItemId: String? = null,

    @SerialName("shared_by")
    val sharedBy: String? = null,

    @SerialName("shared_at")
    val sharedAt: String? = null,

    @SerialName("type")
    val type: String? = null,

    @SerialName("asset_detail")
    val assetDetail: AssetDetail? = null,

)
@Serializable
data class ShareFriendResponse(
    @SerialName("status")
    val status: String? = null,

    @SerialName("data")
    val data: List<ShareFriendData>? = null,

    @SerialName("error")
    val error: String? = null
)

@Serializable
data class ShareFriendData(
    @SerialName("shared_item_id")
    val groupItemId: String? = null,

    @SerialName("shared_by")
    val sharedBy: String? = null,

    @SerialName("shared_at")
    val sharedAt: String? = null,

    @SerialName("type")
    val type: String? = null,

    @SerialName("asset_detail")
    val assetDetail: AssetDetail? = null,

    )

@Serializable
data class AssetDetail(
    // (Common Fields)
    @SerialName("id")
    val id: String? = null,

    @SerialName("name")
    val name: String? = null,

    @SerialName("amount")
    val amount: Double? = null,

    @SerialName("type")
    val type: String? = null,

    @SerialName("image")
    val image: String? = null,

    // ----------------------------------------
    // type = "building"
    // ----------------------------------------
    @SerialName("location_text")
    val locationText: String? = null,


    // ----------------------------------------
    // type = "account"
    // ----------------------------------------
    @SerialName("bank_name")
    val bankName: String? = null,

    @SerialName("account_number")
    val accountNumber: String? = null,

    // ----------------------------------------
    // type = "land"
    // ----------------------------------------
    @SerialName("deed_num")
    val deedNumber: Double? = null,

    @SerialName("area")
    val area: Double? = null,

    @SerialName("location")
    val location: String? = null,

    // ----------------------------------------
    // type = "cash"
    // ----------------------------------------

    // ----------------------------------------
    // type = "insurance"
    // ----------------------------------------
    @SerialName("company_name")
    val companyName: String? = null,

    @SerialName("pol_num")
    val polNumber: String? = null,

    @SerialName("coverage_amount")
    val coverageAmount: Double? = null,

    @SerialName("exp_date_text")
    val expDateText: String? = null,


    // ----------------------------------------
    // type = "investment"
    // ----------------------------------------
    @SerialName("symbol")
    val symbol: String? = null,

    @SerialName("type_name")
    val typeName: String? = null,

    // ----------------------------------------
    // type = "liability"
    // ----------------------------------------
    @SerialName("creditor")
    val creditor: String? = null,

    @SerialName("principal")
    val principal: Double? = null,



    )
