package com.wealthvault.financiallist.ui.debt

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_common_plus
import com.wealthvault.core.generated.resources.ic_nav_debt
import com.wealthvault.core.theme.LightDebt
import com.wealthvault.core.theme.LightSoftWhite
import com.wealthvault.core.utils.formatAmount
import com.wealthvault.core.utils.formatThaiDate
import com.wealthvault.core.utils.getScreenModel
import com.wealthvault.financiallist.ui.FinancialListTemplate
import com.wealthvault.core.components.ConfirmDeleteDialog
import com.wealthvault.financiallist.ui.component.ExpandableCategoryCard
import com.wealthvault.financiallist.ui.component.RealItemCard
import com.wealthvault.financiallist.ui.component.SmartAssetDetailDialog
import com.wealthvault.liability_api.model.GetLiabilityData
import com.wealthvault.liability_api.model.LiabilityIdData
import com.wealthvault_final.`financial-obligations`.ui.menu.ObMenuScreen
import org.jetbrains.compose.resources.painterResource


class DebtScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        var rootNavigator = navigator
        while (rootNavigator.parent != null) {
            rootNavigator = rootNavigator.parent!!
        }

        val screenModel = getScreenModel<DebtScreenModel>()

        LaunchedEffect(Unit) {
            screenModel.fetchLiabilities()
        }

        val loans by screenModel.loans.collectAsState()
        val expenses by screenModel.expenses.collectAsState()

        DebtContent(
            onAddClick = {
                rootNavigator.push(ObMenuScreen())
            },
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
    screenModel: DebtScreenModel
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedLiabilityId by remember { mutableStateOf<String?>(null) }

    // 🌟 State สำหรับ Confirm Dialog
    var showConfirmDelete by remember { mutableStateOf(false) }
    var itemNameToDelete by remember { mutableStateOf("") }

    val filteredExpenses = expenses.filter { it.name.toString().contains(searchQuery, ignoreCase = true) }
    val filteredLoans = loans.filter { it.name.toString().contains(searchQuery, ignoreCase = true) }

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
                if (filteredLoans.isNotEmpty()) {
                    item {
                        ExpandableCategoryCard(title = "หนี้สิน", itemCount = filteredLoans.size, themeColor = "debt", initiallyExpanded = true) {
                            filteredLoans.forEach { loan ->
                                RealItemCard(
                                    title = loan.name ?: "",
                                    subtitleLabel = "เจ้าหนี้", subtitleValue = loan.creditor ?: "",
                                    amountLabel = "ยอดหนี้", amountValue = "${formatAmount(loan.principal ?: 0.0)} บาท",
                                    onClick = { selectedLiabilityId = loan.id }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                if (filteredExpenses.isNotEmpty()) {
                    item {
                        ExpandableCategoryCard(title = "รายจ่ายระยะยาว", itemCount = filteredExpenses.size, themeColor = "debt", initiallyExpanded = true) {
                            filteredExpenses.forEach { exp ->
                                RealItemCard(
                                    title = exp.name ?: "",
                                    subtitleLabel = "เจ้าหนี้", subtitleValue = exp.creditor ?: "",
                                    amountLabel = "ยอดหนี้", amountValue = "${formatAmount(exp.principal ?: 0.0)} บาท",
                                    onClick = { selectedLiabilityId = exp.id }
                                )
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(140.dp)) }
            }
        }

        Surface(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 40.dp, end = 25.dp)
                .size(56.dp)
                .clickable { onAddClick() },
            shape = CircleShape,
            color = LightDebt,
            shadowElevation = 3.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    painter = painterResource(Res.drawable.ic_common_plus),
                    contentDescription = "เพิ่มหนี้สิน",
                    tint = LightSoftWhite,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    }

    // 🌟 ระบบแจ้งเตือนลบ
    if (showConfirmDelete) {
        val annotatedMessage = buildAnnotatedString {
            append("คุณแน่ใจหรือไม่ว่าต้องการลบ ")
            withStyle(style = SpanStyle(color = LightDebt, fontWeight = FontWeight.Bold)) {
                append("'$itemNameToDelete'")
            }
            append(" ออกจากระบบ?")
        }

        ConfirmDeleteDialog(
            title = "ลบหนี้สิน", // หรือจะเช็คจาก type ให้ลึกขึ้นก็ได้ครับ
            message = annotatedMessage,
            onConfirm = {
                selectedLiabilityId?.let { id ->
                    screenModel.deleteLiability(id, "liability")
                }
                showConfirmDelete = false
                selectedLiabilityId = null
            },
            onDismiss = { showConfirmDelete = false }
        )
    }

    // 🌟 เรียกใช้ Smart Dialog ตัวเก่ง
    if (selectedLiabilityId != null && !showConfirmDelete) {
        SmartAssetDetailDialog(
            assetId = selectedLiabilityId!!,
            assetType = "liability", // 🌟 ฟิกซ์ให้เป็น liability เพราะหน้านี้คือหน้าหนี้สิน
            showBottomMenu = true, // หน้าจัดการหนี้สิน ต้องโชว์ปุ่มลบ/แก้ไข
            onDismiss = { selectedLiabilityId = null },
            onDelete = { itemName ->
                itemNameToDelete = itemName // รับชื่อจาก Smart Dialog
                showConfirmDelete = true
            },
            onEdit = {
                // TODO: ไปหน้า Edit หนี้สิน
                selectedLiabilityId = null
            }
        )
    }
}