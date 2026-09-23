package com.wealthvault.financiallist.ui.component

// 🌟 Import UI จาก Core
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.wealthvault.domain.portfolio.BankAccountData
import com.wealthvault.domain.portfolio.BuildingIdData
import com.wealthvault.domain.portfolio.CashIdData
import com.wealthvault.core.components.DetailDialog
import com.wealthvault.core.components.DetailImageRow
import com.wealthvault.core.components.DetailRow
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.utils.formatAmount
import com.wealthvault.core.utils.formatThaiDate
import com.wealthvault.financiallist.usecase.FinanciallistUseCase
import com.wealthvault.domain.portfolio.InsuranceIdData
import com.wealthvault.domain.portfolio.InvestmentIdData
import com.wealthvault.domain.portfolio.LandIdData
import com.wealthvault.domain.portfolio.LiabilityIdData
import com.wealthvault.core.architecture.getOrNull
import com.wealthvault.core.model.Money
import com.wealthvault.core.model.FixedDecimal
import com.wealthvault.financiallist.ui.form.*
import org.koin.compose.koinInject

// 🌟 Helper สำหรับหาชื่อภาษาไทยจาก List ของ Pair
private fun mapTypeLabel(key: String?, mapList: List<Pair<String, String>>): String {
    if (key == null) return "-"
    return mapList.find { it.first == key }?.second ?: key
}

@Composable
fun SmartAssetDetailDialog(
    assetId: String,
    assetType: String,
    useCase: FinanciallistUseCase = koinInject(),
    showBottomMenu: Boolean = false,
    onDismiss: () -> Unit,
    onDelete: (String) -> Unit = {},
    onEdit: (Any) -> Unit = {},
    onShare: () -> Unit = {}
) {
    var isLoading by remember { mutableStateOf(true) }
    var detailData by remember { mutableStateOf<Any?>(null) }

    val themeType = if (assetType.lowercase() == "liability") "debt" else "asset"

    LaunchedEffect(assetId, assetType) {
        isLoading = true
        detailData = when (assetType.lowercase()) {
            "account" -> useCase.getAccountById(assetId).getOrNull()
            "cash" -> useCase.getCashById(assetId).getOrNull()
            "investment" -> useCase.getInvestmentById(assetId).getOrNull()
            "insurance" -> useCase.getInsuranceById(assetId).getOrNull()
            "building" -> useCase.getBuildingById(assetId).getOrNull()
            "land" -> useCase.getLandById(assetId).getOrNull()
            "liability" -> useCase.getLiabilityById(assetId).getOrNull()
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
                    showBottomMenu = showBottomMenu, onDismiss = onDismiss, onDelete = { onDelete(itemData.name ?: "") },
                    onEdit = { onEdit(itemData) },
                    onShare = onShare
                ) {
                    DetailRow("ธนาคาร", itemData.bankName)
                    DetailRow("เลขบัญชี", itemData.bankAccount)
                    // 🌟 Map ประเภทบัญชี
                    DetailRow("ประเภท", mapTypeLabel(itemData.type, bankAccountTypes))
                    DetailRow("ยอดเงิน", "${formatAmount(itemData.amount ?: Money(0))} บาท", isHighlight = true)
                    DetailRow("คำอธิบาย", itemData.description ?: "-", isLast = itemData.files.isNullOrEmpty())
                    DetailImageRow(files = itemData.files)
                }
            }

            is CashIdData -> {
                DetailDialog(
                    subtitle = subtitleText, title = itemData.name ?: "", updatedAt = formatThaiDate(itemData.updatedAt), themeType = themeType,
                    showBottomMenu = showBottomMenu, onDismiss = onDismiss, onDelete = { onDelete(itemData.name ?: "") },
                    onEdit = { onEdit(itemData) },
                    onShare = onShare
                ) {
                    DetailRow("มูลค่า", "${formatAmount(itemData.amount ?: Money(0))} บาท", isHighlight = true)
                    DetailRow("คำอธิบาย", itemData.description ?: "-", isLast = itemData.files.isNullOrEmpty())
                    DetailImageRow(files = itemData.files)
                }
            }

            is InvestmentIdData -> {
                // 🌟 โชว์แค่ชื่อเท่านั้น ไม่มีวงเล็บต่อท้ายแล้ว
                val displayTitle = itemData.name ?: "ไม่ระบุชื่อ"

                DetailDialog(
                    subtitle = subtitleText,
                    title = displayTitle,
                    updatedAt = formatThaiDate(itemData.updatedAt),
                    themeType = themeType,
                    showBottomMenu = showBottomMenu,
                    onDismiss = onDismiss,
                    onDelete = { onDelete(displayTitle) },
                    onEdit = { onEdit(itemData) },
                    onShare = onShare
                ) {
                    DetailRow("โบรกเกอร์", itemData.brokerName)

                    // 🌟 เพิ่มบรรทัดนี้: เอา Symbol มาแสดงเป็น DetailRow (ถ้ามีค่า)
                    if (!itemData.symbol.isNullOrEmpty()) {
                        DetailRow("สัญลักษณ์", itemData.symbol)
                    }

                    DetailRow("จำนวน", itemData.quantity?.decimalString() ?: "0")
                    DetailRow("ราคาทุนต่อหน่วย", "${formatAmount(itemData.costPerPrice ?: Money(0))} บาท")
                    // 🌟 Map ประเภทการลงทุน
                    DetailRow("ประเภท", mapTypeLabel(itemData.type, investmentTypes))
                    val totalValue = itemData.amount
                        ?: itemData.costPerPrice?.times(itemData.quantity ?: FixedDecimal(0, 4))
                        ?: Money(0)
                    DetailRow("มูลค่ารวม", "${formatAmount(totalValue)} บาท", isHighlight = true)
                    DetailRow("คำอธิบาย", itemData.description ?: "-", isLast = itemData.files.isNullOrEmpty())
                    DetailImageRow(files = itemData.files)
                }
            }

            is InsuranceIdData -> {
                DetailDialog(
                    subtitle = subtitleText, title = itemData.name ?: "", updatedAt = formatThaiDate(itemData.updatedAt), themeType = themeType,
                    showBottomMenu = showBottomMenu, onDismiss = onDismiss, onDelete = { onDelete(itemData.name ?: "") },
                    onEdit = { onEdit(itemData) },
                    onShare = onShare
                ) {
                    DetailRow("เลขกรมธรรม์", itemData.policyNumber)
                    DetailRow("บริษัท", itemData.companyName)
                    DetailRow("วงเงินคุ้มครอง", "${formatAmount(itemData.coverageAmount ?: Money(0))} บาท", isHighlight = true)
                    DetailRow("ระยะเวลาคุ้มครอง", "${itemData.coveragePeriod} ปี")
                    // 🌟 Map ประเภทประกัน
                    DetailRow("ประเภทประกัน", mapTypeLabel(itemData.type, insuranceTypes))
                    DetailRow("วันเริ่มสัญญา", formatThaiDate(itemData.conDate))
                    DetailRow("วันสิ้นสุดสัญญา", formatThaiDate(itemData.expDate))
                    DetailRow("คำอธิบาย", itemData.description ?: "-", isLast = itemData.files.isNullOrEmpty())
                    DetailImageRow(files = itemData.files)
                }
            }

            is BuildingIdData -> {
                DetailDialog(
                    subtitle = subtitleText, title = itemData.name ?: "", updatedAt = formatThaiDate(itemData.updatedAt), themeType = themeType,
                    showBottomMenu = showBottomMenu, onDismiss = onDismiss, onDelete = { onDelete(itemData.name ?: "") },
                    onEdit = { onEdit(itemData) },
                    onShare = onShare
                ) {
                    // 🌟 Map ประเภทสิ่งปลูกสร้าง
                    DetailRow("ประเภท", mapTypeLabel(itemData.type, buildingTypes))
                    DetailRow("พื้นที่", "${formatAmount(itemData.area ?: 0.0)} ตร.ม.")
                    DetailRow("มูลค่าประเมิน", "${formatAmount(itemData.amount ?: Money(0))} บาท", isHighlight = true)
                    val addressStr = itemData.location?.let { "${it.address} ${it.subDistrict} ${it.district} ${it.province} ${it.postalCode}".trim() } ?: "-"
                    DetailRow("ที่อยู่", addressStr)
                    DetailRow("คำอธิบาย", itemData.description ?: "-", isLast = itemData.files.isNullOrEmpty())
                    DetailImageRow(files = itemData.files)
                }
            }

            is LandIdData -> {
                DetailDialog(
                    subtitle = subtitleText, title = itemData.name ?: "", updatedAt = formatThaiDate(itemData.updatedAt), themeType = themeType,
                    showBottomMenu = showBottomMenu, onDismiss = onDismiss, onDelete = { onDelete(itemData.name ?: "") },
                    onEdit = { onEdit(itemData) },
                    onShare = onShare
                ) {
                    DetailRow("เลขโฉนด", itemData.deedNum)
                    DetailRow("ขนาดพื้นที่", "${formatAmount(itemData.area ?: 0.0)} ตารางวา")
                    DetailRow("มูลค่าประเมิน", "${formatAmount(itemData.amount ?: Money(0))} บาท", isHighlight = true)
                    val addressStr = itemData.location?.let { "${it.address} ${it.subDistrict} ${it.district} ${it.province} ${it.postalCode}".trim() } ?: "-"
                    DetailRow("ที่อยู่", addressStr)
                    DetailRow("คำอธิบาย", itemData.description ?: "-", isLast = itemData.files.isNullOrEmpty())
                    DetailImageRow(files = itemData.files)
                }
            }

            is LiabilityIdData -> {
                val safeName = itemData.name ?: "-"

                DetailDialog(
                    subtitle = subtitleText, title = safeName, updatedAt = formatThaiDate(itemData.updatedAt), themeType = themeType,
                    showBottomMenu = showBottomMenu, onDismiss = onDismiss, onDelete = { onDelete(safeName) },
                    onEdit = { onEdit(itemData) },
                    onShare = onShare
                ) {
                    val isLoan = itemData.type == "LIABILITY_TYPE_LOAN"

                    DetailRow("เจ้าหนี้", itemData.creditor)

                    // 🌟 Map ประเภทหนี้สิน หรือ รายจ่าย
                    val mappedType = if (isLoan) mapTypeLabel(itemData.type, liabilityTypes)
                    else mapTypeLabel(itemData.type, expenseTypes)
                    DetailRow("ประเภท", mappedType)

                    DetailRow("เงินต้น/ยอดหนี้", "${formatAmount(itemData.principal ?: Money(0))} บาท", isHighlight = true)

                    val rate = itemData.interestRate
                    if (rate != null && rate.unscaled != 0L) {
                        DetailRow(label = "ดอกเบี้ย", value = "${rate.decimalString()} %")
                    }

                    DetailRow(
                        label = "วันที่เริ่มต้น",
                        value = if (!itemData.startedAt.isNullOrBlank()) formatThaiDate(itemData.startedAt) else "-"
                    )

                    if (isLoan) {
                        DetailRow(
                            label = "วันที่สิ้นสุด",
                            value = if (!itemData.endedAt.isNullOrBlank()) formatThaiDate(itemData.endedAt) else "-"
                        )
                    }

                    DetailRow("คำอธิบาย", itemData.description ?: "-", isLast = itemData.files.isNullOrEmpty())
                    DetailImageRow(files = itemData.files)
                }
            }
        }
    }
}
