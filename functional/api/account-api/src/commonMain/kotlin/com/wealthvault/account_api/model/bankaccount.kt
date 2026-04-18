package com.wealthvault.account_api.model

import com.wealthvault.core.model.FileDataModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ตัว Request สำหรับ Create/Update ปล่อยไว้เหมือนเดิมได้ (ถ้าเพื่อนยังใช้แบบนี้)
@Serializable
data class BankAccountRequest(


    @SerialName("name")
    val name: String,

    @SerialName("bank_name")
    val bankName: String,

    @SerialName("bank_account")
    val bankAccount: String,

    @SerialName("type")
    val type: String,

    @SerialName("amount")
    val amount: Double,

    @SerialName("description")
    val description: String,

    @SerialName("files")
    val files:List<BankAccountFileUploadData> = emptyList(),

    val deleteListId : List<String>? = emptyList()


)

// ตัวรับ Response (เพิ่ม Files และเปลี่ยน amount)
@Serializable
data class BankAccountResponse(
    @SerialName("status") val status: String? = null,
    @SerialName("data") val data: BankAccountData? = null,
    @SerialName("error") val error: String? = null
)

@Serializable
data class BankAccountData(
    @SerialName("id") val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("name") val name: String,
    @SerialName("bank_name") val bankName: String,
    @SerialName("bank_account") val bankAccount: String,
    @SerialName("type") val type: String,
    @SerialName("amount") val amount: Double, // 🌟 เปลี่ยนเป็น Double
    @SerialName("description") val description: String,

    // 🌟 เพิ่มฟิลด์ใหม่จาก JSON
    @SerialName("files") val files: List<FileDataModel>? = emptyList(),
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)

// 🌟 Shared Model สำหรับรูปภาพ


@Serializable
data class BankAccountFileUploadData(
    val bytes: ByteArray,
    val mimeType: String,
    val fileName: String
)
