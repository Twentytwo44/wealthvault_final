package com.wealthvault.social.ui.components.profile // หรือโฟลเดอร์ที่คุณ Champ ต้องการในฝั่ง Social

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import org.koin.compose.koinInject

// 🌟 Import UI จาก Core
import com.wealthvault.core.components.DetailDialog
import com.wealthvault.core.components.DetailRow
import com.wealthvault.core.components.DetailImageRow
import com.wealthvault.core.theme.LightPrimary

// 🌟 Import Utils & Models
import com.wealthvault.core.utils.formatAmount
import com.wealthvault.core.utils.formatThaiDate
import com.wealthvault.social.data.SocialRepositoryImpl
import com.wealthvault.account_api.model.BankAccountData
import com.wealthvault.cash_api.model.CashIdData
import com.wealthvault.investment_api.model.InvestmentIdData
import com.wealthvault.insurance_api.model.InsuranceIdData
import com.wealthvault.building_api.model.BuildingIdData
import com.wealthvault.land_api.model.LandIdData
import com.wealthvault.liability_api.model.LiabilityIdData

@Composable
fun SmartAssetDetailDialog(
    assetId: String,
    assetType: String,
    repository: SocialRepositoryImpl = koinInject(),
    showBottomMenu: Boolean = false,
    onDismiss: () -> Unit,
    onDelete: (String) -> Unit = {},
    onEdit: () -> Unit = {},
    onShare: () -> Unit = {}
) {
    var isLoading by remember { mutableStateOf(true) }
    var detailData by remember { mutableStateOf<Any?>(null) }

    val themeType = if (assetType.lowercase() == "liability") "debt" else "asset"

    LaunchedEffect(assetId, assetType) {
        isLoading = true
        detailData = when (assetType.lowercase()) {
            "account" -> repository.getAccountById(assetId).getOrNull()
            "cash" -> repository.getCashById(assetId).getOrNull()
            "investment" -> repository.getInvestmentById(assetId).getOrNull()
            "insurance" -> repository.getInsuranceById(assetId).getOrNull()
            "building" -> repository.getBuildingById(assetId).getOrNull()
            "land" -> repository.getLandById(assetId).getOrNull()
            "liability" -> repository.getLiabilityById(assetId).getOrNull()
            else -> null
        }
        isLoading = false
    }

    if (isLoading) {
        Dialog(onDismissRequest = onDismiss) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(Color.White, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = LightPrimary)
            }
        }
    } else if (detailData != null) {

        val subtitleText = when (assetType.lowercase()) {
            "account" -> "ทรัพย์สิน · บัญชีเงินฝาก"
            "insurance" -> "ทรัพย์สิน · ประกัน"
            "land" -> "ทรัพย์สิน · ที่ดิน"
            "building" -> "ทรัพย์สิน · บ้าน ตึก อาคาร"
            "investment" -> "ทรัพย์สิน · ลงทุน หุ้น กองทุน"
            "cash" -> "ทรัพย์สิน · เงินสด ทองคำ"
            "liability" -> {
                val liabilityData = detailData as? LiabilityIdData
                if (liabilityData?.type == "LIABILITY_TYPE_LOAN") "หนี้สิน · รายละเอียดหนี้สิน"
                else "หนี้สิน · รายละเอียดรายจ่าย"
            }
            else -> "รายละเอียดทรัพย์สิน"
        }

        when (val itemData = detailData!!) {
            is BankAccountData -> {
                DetailDialog(
                    subtitle = subtitleText, title = itemData.name, updatedAt = formatThaiDate(itemData.updatedAt), themeType = themeType,
                    showBottomMenu = showBottomMenu, onDismiss = onDismiss, onDelete = { onDelete(itemData.name) }, onEdit = onEdit, onShare = onShare
                ) {
                    DetailRow("ธนาคาร", itemData.bankName)
                    DetailRow("เลขบัญชี", itemData.bankAccount)
                    DetailRow("ประเภท", itemData.type)
                    DetailRow("ยอดเงิน", "${formatAmount(itemData.amount ?: 0.0)} บาท", isHighlight = true)
                    DetailRow("คำอธิบาย", itemData.description ?: "-", isLast = itemData.files.isNullOrEmpty())
                    DetailImageRow(files = itemData.files)
                }
            }

            is CashIdData -> {
                DetailDialog(
                    subtitle = subtitleText, title = itemData.name ?: "", updatedAt = formatThaiDate(itemData.updatedAt), themeType = themeType,
                    showBottomMenu = showBottomMenu, onDismiss = onDismiss, onDelete = { onDelete(itemData.name ?: "") }, onEdit = onEdit, onShare = onShare
                ) {
                    DetailRow("มูลค่า", "${formatAmount(itemData.amount ?: 0.0)} บาท", isHighlight = true)
                    DetailRow("คำอธิบาย", itemData.description ?: "-", isLast = itemData.files.isNullOrEmpty())
                    DetailImageRow(files = itemData.files)
                }
            }

            is InvestmentIdData -> {
                DetailDialog(
                    subtitle = subtitleText, title = "${itemData.name} (${itemData.symbol})", updatedAt = formatThaiDate(itemData.updatedAt), themeType = themeType,
                    showBottomMenu = showBottomMenu, onDismiss = onDismiss, onDelete = { onDelete("${itemData.name} (${itemData.symbol})") }, onEdit = onEdit, onShare = onShare
                ) {
                    DetailRow("โบรกเกอร์", itemData.brokerName)
                    DetailRow("จำนวน", formatAmount(itemData.quantity ?: 0.0))
                    DetailRow("ราคาทุนต่อหน่วย", "${formatAmount(itemData.costPerPrice ?: 0.0)} บาท")
                    DetailRow("ประเภท", itemData.type)
                    DetailRow("มูลค่ารวม", "${formatAmount((itemData.quantity ?: 0.0) * (itemData.costPerPrice ?: 0.0))} บาท", isHighlight = true)
                    DetailRow("คำอธิบาย", itemData.description ?: "-", isLast = itemData.files.isNullOrEmpty())
                    DetailImageRow(files = itemData.files)
                }
            }

            is InsuranceIdData -> {
                DetailDialog(
                    subtitle = subtitleText, title = itemData.name ?: "", updatedAt = formatThaiDate(itemData.updatedAt), themeType = themeType,
                    showBottomMenu = showBottomMenu, onDismiss = onDismiss, onDelete = { onDelete(itemData.name ?: "") }, onEdit = onEdit, onShare = onShare
                ) {
                    DetailRow("เลขกรมธรรม์", itemData.policyNumber)
                    DetailRow("บริษัท", itemData.companyName)
                    DetailRow("วงเงินคุ้มครอง", "${formatAmount(itemData.coverageAmount ?: 0.0)} บาท", isHighlight = true)
                    DetailRow("ระยะเวลาคุ้มครอง", "${itemData.coveragePeriod} ปี")
                    DetailRow("วันเริ่มสัญญา", formatThaiDate(itemData.conDate))
                    DetailRow("วันสิ้นสุดสัญญา", formatThaiDate(itemData.expDate))
                    DetailRow("คำอธิบาย", itemData.description ?: "-", isLast = itemData.files.isNullOrEmpty())
                    DetailImageRow(files = itemData.files)
                }
            }

            is BuildingIdData -> {
                DetailDialog(
                    subtitle = subtitleText, title = itemData.name ?: "", updatedAt = formatThaiDate(itemData.updatedAt), themeType = themeType,
                    showBottomMenu = showBottomMenu, onDismiss = onDismiss, onDelete = { onDelete(itemData.name ?: "") }, onEdit = onEdit, onShare = onShare
                ) {
                    DetailRow("ประเภท", itemData.type ?: "")
                    DetailRow("พื้นที่", "${formatAmount(itemData.area ?: 0.0)} ตร.ม.")
                    DetailRow("มูลค่าประเมิน", "${formatAmount(itemData.amount ?: 0.0)} บาท", isHighlight = true)
                    val addressStr = itemData.location?.let { "${it.address} ${it.subDistrict} ${it.district} ${it.province} ${it.postalCode}".trim() } ?: "-"
                    DetailRow("ที่อยู่", addressStr)
                    DetailRow("คำอธิบาย", itemData.description ?: "-", isLast = itemData.files.isNullOrEmpty())
                    DetailImageRow(files = itemData.files)
                }
            }

            is LandIdData -> {
                DetailDialog(
                    subtitle = subtitleText, title = itemData.name ?: "", updatedAt = formatThaiDate(itemData.updatedAt), themeType = themeType,
                    showBottomMenu = showBottomMenu, onDismiss = onDismiss, onDelete = { onDelete(itemData.name ?: "") }, onEdit = onEdit, onShare = onShare
                ) {
                    DetailRow("เลขโฉนด", itemData.deedNum)
                    DetailRow("ขนาดพื้นที่", "${formatAmount(itemData.area ?: 0.0)} ตารางวา")
                    DetailRow("มูลค่าประเมิน", "${formatAmount(itemData.amount ?: 0.0)} บาท", isHighlight = true)
                    val addressStr = itemData.location?.let { "${it.address} ${it.subDistrict} ${it.district} ${it.province} ${it.postalCode}".trim() } ?: "-"
                    DetailRow("ที่อยู่", addressStr)
                    DetailRow("คำอธิบาย", itemData.description ?: "-", isLast = itemData.files.isNullOrEmpty())
                    DetailImageRow(files = itemData.files)
                }
            }

            is LiabilityIdData -> {
                DetailDialog(
                    subtitle = subtitleText, title = itemData.name ?: "", updatedAt = formatThaiDate(itemData.updatedAt), themeType = themeType,
                    showBottomMenu = showBottomMenu, onDismiss = onDismiss, onDelete = { onDelete(itemData.name ?: "") }, onEdit = onEdit, onShare = onShare
                ) {
                    DetailRow("เจ้าหนี้", itemData.creditor)
                    DetailRow("ประเภท", if (itemData.type == "LIABILITY_TYPE_LOAN") "หนี้สิน" else "รายจ่าย")
                    DetailRow("เงินต้น/ยอดหนี้", "${formatAmount(itemData.principal ?: 0.0)} บาท", isHighlight = true)
                    DetailRow("คำอธิบาย", itemData.description ?: "-", isLast = itemData.files.isNullOrEmpty())
                    DetailImageRow(files = itemData.files)
                }
            }
        }

    }else {
        // 🌟 พระเอกของเราคือตรงนี้ครับ! เพิ่มเงื่อนไข else ดักกรณีที่หาข้อมูลไม่เจอ (detailData == null)
        AlertDialog(
            onDismissRequest = onDismiss,
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp),
            title = {
                Text(
                    text = "ไม่พบข้อมูล",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3A2F2A)
                )
            },
            text = {
                Text(
                    text = "รายการทรัพย์สินนี้อาจถูกลบไปแล้ว หรือไม่มีอยู่ในระบบ",
                    color = Color.Gray
                )
            },
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text(
                        text = "ตกลง",
                        color = LightPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        )
    }
}