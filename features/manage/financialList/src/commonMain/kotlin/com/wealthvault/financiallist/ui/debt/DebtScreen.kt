package com.wealthvault.financiallist.ui.debt

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_nav_debt
import com.wealthvault.core.theme.LightDebt
import com.wealthvault.core.utils.getScreenModel
import com.wealthvault.financiallist.ui.FinancialListTemplate
import com.wealthvault.financiallist.ui.component.ExpandableCategoryCard
import com.wealthvault.financiallist.ui.component.RealItemCard
import com.wealthvault.financiallist.ui.component.DetailDialog // 🌟 Import Dialog
import com.wealthvault.financiallist.ui.component.DetailRow    // 🌟 Import Row
import org.jetbrains.compose.resources.painterResource

// 🌟 Import เพิ่มเติมสำหรับปุ่มเพิ่มรายการ
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.foundation.clickable
import com.wealthvault.core.theme.LightSoftWhite
import com.wealthvault.core.generated.resources.ic_common_plus

// 🌟 Import Data Class
import com.wealthvault.liability_api.model.GetLiabilityData
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.wealthvault.core.utils.formatAmount
import com.wealthvault.core.utils.formatThaiDate
import com.wealthvault.financiallist.ui.component.ConfirmDeleteDialog
import com.wealthvault.financiallist.ui.component.DetailImageRow // 🌟 นำเข้าโชว์รูปภาพ
import com.wealthvault.liability_api.model.LiabilityIdData
import kotlinx.atomicfu.TraceBase.None.append
import kotlin.math.roundToLong

class DebtScreen(private val onAddClick: () -> Unit) : Screen {
    @Composable
    override fun Content() {
        val screenModel = getScreenModel<DebtScreenModel>()

        LaunchedEffect(Unit) {
            screenModel.fetchLiabilities()
        }

        val loans by screenModel.loans.collectAsState()
        val expenses by screenModel.expenses.collectAsState()

        DebtContent(
            onAddClick = onAddClick,
            loans = loans,
            expenses = expenses,
            screenModel = screenModel
        )
    }
}

@Composable
fun DebtContent(
    onAddClick: () -> Unit,
    loans: List<GetLiabilityData>,
    expenses: List<GetLiabilityData>,
    screenModel: DebtScreenModel // 🌟 รับ Model เข้ามาเพื่อยิง API
) {
    var searchQuery by remember { mutableStateOf("") }

    // 🌟 เปลี่ยนมาเก็บแค่ ID แบบหน้า Asset
    var selectedLiabilityId by remember { mutableStateOf<String?>(null) }

    val filteredExpenses = expenses.filter { it.name.contains(searchQuery, ignoreCase = true) }
    val filteredLoans = loans.filter { it.name.contains(searchQuery, ignoreCase = true) }

    // 🌟 1. ใช้ Box ครอบทั้งหน้าเพื่อวางปุ่มลอย
    Box(modifier = Modifier.fillMaxSize()) {

        FinancialListTemplate(
            headerTitle = "หนี้สิน & รายจ่ายระยะยาว",
            themeColor = LightDebt,
            searchQuery = searchQuery,
            onSearchChange = { searchQuery = it },
            onAddClick = onAddClick,
            headerIcon = {
                Icon(
                    painter = painterResource(Res.drawable.ic_nav_debt),
                    contentDescription = null,
                    tint = LightDebt,
                    modifier = Modifier.padding(horizontal = 4.dp).size(28.dp)
                )
            }
        ) {
            LazyColumn {
                // 🌟 1. หนี้สิน
                if (filteredLoans.isNotEmpty()) {
                    item {
                        ExpandableCategoryCard(title = "หนี้สิน", itemCount = filteredLoans.size, themeColor = "debt", initiallyExpanded = true) {
                            filteredLoans.forEach { loan ->
                                RealItemCard(
                                    title = loan.name,
                                    subtitleLabel = "เจ้าหนี้", subtitleValue = loan.creditor,
                                    amountLabel = "ยอดหนี้", amountValue = "${formatAmount(loan.principal)} บาท",
                                    onClick = { selectedLiabilityId = loan.id }
                                )
                            }
                        }
                    }
                }

                // 🌟 2. รายจ่ายระยะยาว
                if (filteredExpenses.isNotEmpty()) {
                    item {
                        ExpandableCategoryCard(title = "รายจ่ายระยะยาว", itemCount = filteredExpenses.size, themeColor = "debt", initiallyExpanded = true) {
                            filteredExpenses.forEach { exp ->
                                RealItemCard(
                                    title = exp.name,
                                    subtitleLabel = "เจ้าหนี้", subtitleValue = exp.creditor,
                                    amountLabel = "ยอดหนี้", amountValue = "${formatAmount(exp.principal)} บาท",
                                    onClick = { selectedLiabilityId = exp.id }
                                )
                            }
                        }
                    }
                }

                // 🌟 ปรับความสูงตรงนี้เผื่อให้ดึง List ขึ้นมาพ้นปุ่มได้ (ถ้า 80.dp ไม่พอ ลองปรับเป็น 100.dp ได้ครับ)
                item { Spacer(modifier = Modifier.height(140.dp)) }
            }
        }

        // 🌟 2. ปุ่ม Floating Action Button ของคุณ Champ
        Surface(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 40.dp, end = 25.dp) // 💡 ถ้าติด Navigation Bar ด้านล่าง อาจจะต้องเพิ่ม bottom นิดนึงนะครับ
                .size(56.dp)
                .clickable { onAddClick() },
            shape = CircleShape,
            color = LightDebt,
            shadowElevation = 3.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    painter = painterResource(Res.drawable.ic_common_plus),
                    contentDescription = "เพิ่มหนี้สิน", // 🌟 เปลี่ยนเป็นหนี้สินให้ตรงหน้า
                    tint = LightSoftWhite,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    }

    // 🌟 เรียกใช้ FetcherDialog เมื่อมีการกดการ์ด (เอาไว้นอก Box เพราะเป็น Dialog ลอยทับหน้าจออยู่แล้ว)
    if (selectedLiabilityId != null) {
        DebtDetailFetcherDialog(
            liabilityId = selectedLiabilityId!!,
            screenModel = screenModel,
            onDismiss = { selectedLiabilityId = null }
        )
    }
}

@Composable
fun DebtDetailFetcherDialog(
    liabilityId: String,
    screenModel: DebtScreenModel,
    onDismiss: () -> Unit
) {
    var isLoading by remember { mutableStateOf(true) }
    var detailData by remember { mutableStateOf<LiabilityIdData?>(null) }

    // 🌟 1. เพิ่ม State สำหรับคุมการเปิด/ปิด และเก็บชื่อที่จะลบ
    var showConfirmDelete by remember { mutableStateOf(false) }
    var itemNameToDelete by remember { mutableStateOf("") }

    LaunchedEffect(liabilityId) {
        isLoading = true
        detailData = screenModel.getLiabilityById(liabilityId)
        isLoading = false
    }

    if (isLoading) {
        Dialog(onDismissRequest = onDismiss) {
            Box(
                modifier = Modifier.size(100.dp).background(Color.White, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = LightDebt)
            }
        }
    } else if (detailData != null) {
        val item = detailData!!
        val isLoan = item.type == "LIABILITY_TYPE_LOAN"

        // 🌟 2. Popup ยืนยันการลบแบบไฮไลต์ชื่อเป็นสีแดง (LightDebt)
        if (showConfirmDelete) {
            val annotatedMessage = buildAnnotatedString {
                append("คุณแน่ใจหรือไม่ว่าต้องการลบ ")
                withStyle(style = SpanStyle(
                    color = LightDebt, // 🌟 ใช้สีแดงตามธีมหนี้สิน
                    fontWeight = FontWeight.Bold
                )
                ) {
                    append("'$itemNameToDelete'")
                }
                append(" ออกจากระบบ?")
            }

            ConfirmDeleteDialog(
                title = if (isLoan) "ลบหนี้สิน" else "ลบรายจ่าย",
                message = annotatedMessage, // 🌟 ส่ง AnnotatedString เข้าไป
                onConfirm = {
                    screenModel.deleteLiability(item.id, "liability")
                    showConfirmDelete = false
                    onDismiss()
                },
                onDismiss = { showConfirmDelete = false }
            )
        }

        DetailDialog(
            subtitle = if (isLoan) "หนี้สิน · รายละเอียดหนี้สิน" else "หนี้สิน · รายละเอียดรายจ่าย",
            title = item.name,
            updatedAt = formatThaiDate(item.updatedAt),
            themeType = "debt",
            onDismiss = onDismiss,
            onDelete = {
                // 🌟 3. เก็บชื่อรายการก่อนเปิด Popup
                itemNameToDelete = item.name
                showConfirmDelete = true
            }
        ) {
            DetailRow("เจ้าหนี้", item.creditor)
            DetailRow("ยอดหนี้คงเหลือ", "${formatAmount(item.principal)} บาท")
            DetailRow("อัตราดอกเบี้ย", "${item.interestRate}%")

            item.startedAt?.takeIf { it.isNotEmpty() }?.let { date ->
                DetailRow("เริ่มทำสัญญา", formatThaiDate(date))
            }
            item.endedAt?.takeIf { it.isNotEmpty() }?.let { date ->
                DetailRow("สิ้นสุดสัญญา", formatThaiDate(date))
            }

            DetailRow("คำอธิบาย", item.description, isLast = item.files.isNullOrEmpty())

            DetailImageRow(files = item.files)
        }
    }
}