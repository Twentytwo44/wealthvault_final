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
            expenses = expenses
        )
    }
}

@Composable
fun DebtContent(
    onAddClick: () -> Unit,
    loans: List<GetLiabilityData>,
    expenses: List<GetLiabilityData>
) {
    var searchQuery by remember { mutableStateOf("") }
    // 🌟 State สำหรับเก็บข้อมูลรายการที่เลือกเพื่อเปิด Popup
    var selectedItem by remember { mutableStateOf<GetLiabilityData?>(null) }

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
                                onClick = { selectedItem = loan } // 🌟 เพิ่ม onClick
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
                                onClick = { selectedItem = exp } // 🌟 เพิ่ม onClick
                            )
                        }
                    }
                }
            }



            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }

    // 🌟 แสดง Popup เมื่อมีการเลือก Item
    selectedItem?.let { item ->
        DetailDialog(
            subtitle = if (item.type == "LIABILITY_TYPE_LOAN") "หนี้สิน · รายละเอียดหนี้สิน" else "หนี้สิน · รายละเอียดรายจ่าย",
            title = item.name,
            themeType = "debt",
            onDismiss = { selectedItem = null }
        ) {
            DetailRow("เจ้าหนี้", item.creditor)
            DetailRow("ยอดหนี้คงเหลือ", "${item.principal} บาท")
            DetailRow("อัตราดอกเบี้ย", "${item.interestRate}%")
            DetailRow("คำอธิบาย", item.description, isLast = true)
        }
    }
}