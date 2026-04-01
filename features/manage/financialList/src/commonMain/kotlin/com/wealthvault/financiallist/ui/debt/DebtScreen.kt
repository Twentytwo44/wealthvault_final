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

// 🌟 Import Data Class
import com.wealthvault.liability_api.model.GetLiabilityData
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import com.wealthvault.financiallist.ui.component.DetailImageRow // 🌟 นำเข้าโชว์รูปภาพ
import com.wealthvault.liability_api.model.LiabilityIdData


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
                                amountLabel = "ยอดหนี้", amountValue = "${loan.principal} บาท",
                                onClick = { selectedLiabilityId = loan.id } // 🌟 เก็บแค่ ID
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
                                amountLabel = "ยอดหนี้", amountValue = "${exp.principal} บาท",
                                onClick = { selectedLiabilityId = exp.id } // 🌟 เก็บแค่ ID
                            )
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }

    // 🌟 เรียกใช้ FetcherDialog เมื่อมีการกดการ์ด
    if (selectedLiabilityId != null) {
        DebtDetailFetcherDialog(
            liabilityId = selectedLiabilityId!!,
            screenModel = screenModel,
            onDismiss = { selectedLiabilityId = null }
        )
    }
}

// 🌟 สร้าง Dialog แบบมี Loading เพื่อยิง API รายตัว
@Composable
fun DebtDetailFetcherDialog(
    liabilityId: String,
    screenModel: DebtScreenModel,
    onDismiss: () -> Unit
) {
    var isLoading by remember { mutableStateOf(true) }
    var detailData by remember { mutableStateOf<LiabilityIdData?>(null) }

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
                CircularProgressIndicator(color = LightDebt) // 🌟 สีโหลดดิ่งของหมวดหนี้
            }
        }
    } else if (detailData != null) {
        val item = detailData!!
        val isLoan = item.type == "LIABILITY_TYPE_LOAN"

        DetailDialog(
            subtitle = if (isLoan) "หนี้สิน · รายละเอียดหนี้สิน" else "หนี้สิน · รายละเอียดรายจ่าย",
            title = item.name,
            themeType = "debt", // 🌟 ใช้สีของหนี้สิน
            onDismiss = onDismiss
        ) {
            DetailRow("เจ้าหนี้", item.creditor)
            DetailRow("ยอดหนี้คงเหลือ", "${item.principal} บาท")
            DetailRow("อัตราดอกเบี้ย", "${item.interestRate}%")

            item.startedAt?.takeIf { it.isNotEmpty() }?.let { date ->
                DetailRow("เริ่มทำสัญญา", date.take(10))
            }
            item.endedAt?.takeIf { it.isNotEmpty() }?.let { date ->
                DetailRow("สิ้นสุดสัญญา", date.take(10))
            }

            DetailRow("คำอธิบาย", item.description, isLast = item.files.isNullOrEmpty())

            // 🌟 โชว์รูปภาพ (เช่น รูปสัญญากู้ยืม)
            DetailImageRow(files = item.files)
        }
    }
}