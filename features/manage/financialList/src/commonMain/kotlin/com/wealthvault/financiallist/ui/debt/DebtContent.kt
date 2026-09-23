package com.wealthvault.financiallist.ui.debt

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wealthvault.core.components.ConfirmDeleteDialog
import com.wealthvault.core.architecture.AppError
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_common_plus
import com.wealthvault.core.generated.resources.ic_nav_debt
import com.wealthvault.core.theme.LightDebt
import com.wealthvault.core.theme.LightSoftWhite
import com.wealthvault.core.utils.LocalRootNavigator
import com.wealthvault.core.utils.formatAmount
import com.wealthvault.core.utils.getScreenModel
import com.wealthvault.core.model.Money
import com.wealthvault.core.navigation.SharedScreen
import com.wealthvault.financiallist.ui.FinancialListTemplate
import com.wealthvault.financiallist.ui.FinancialListEmptyState
import com.wealthvault.financiallist.ui.FinancialListNoResultsState
import com.wealthvault.financiallist.ui.component.ExpandableCategoryCard
import com.wealthvault.financiallist.ui.component.RealItemCard
import com.wealthvault.financiallist.ui.component.SmartAssetDetailDialog
import com.wealthvault.financiallist.ui.debt.form.debt.LiabilityFormScreen
import com.wealthvault.financiallist.ui.debt.form.expense.ExpenseFormScreen
import com.wealthvault.financiallist.ui.shareasset.ShareAssetScreen
import com.wealthvault.domain.portfolio.GetLiabilityData
import com.wealthvault.domain.portfolio.LiabilityIdData
import com.wealthvault.financiallist.ui.toAttachment
import com.wealthvault.domain.portfolio.ExpenseModel
import com.wealthvault.domain.portfolio.LiabilityModel
import org.jetbrains.compose.resources.painterResource

@Composable
fun DebtContent(
    onAddClick: () -> Unit,
    loans: List<GetLiabilityData>,
    expenses: List<GetLiabilityData>,
    screenModel: DebtScreenModel,
    navigatorContent: Navigator
) {
    val uiState by screenModel.uiState.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }
    var selectedLiabilityId by remember { mutableStateOf<String?>(null) }

    // 🌟 State สำหรับ Confirm Dialog
    var showConfirmDelete by remember { mutableStateOf(false) }
    var itemNameToDelete by remember { mutableStateOf("") }

    // ค้นหารายการ (แก้ให้ .name ไม่เป็น nullable เพราะใน Data Class เราตั้งให้เป็นค่า non-null แล้ว)
    val filteredExpenses = remember(expenses, searchQuery) {
        expenses.filter { (it.name ?: "").contains(searchQuery, ignoreCase = true) }
    }
    val filteredLoans = remember(loans, searchQuery) {
        loans.filter { (it.name ?: "").contains(searchQuery, ignoreCase = true) }
    }

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
            val hasData = loans.isNotEmpty() || expenses.isNotEmpty()
            if (uiState.isLoading && !hasData) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = LightDebt)
                }
            } else if (!hasData && uiState.error != null) {
                val error = requireNotNull(uiState.error)
                DebtLoadError(
                    message = debtErrorMessage(error),
                    onRetry = { screenModel.onAction(DebtUiAction.Refresh) },
                )
            } else if (!hasData) {
                FinancialListEmptyState(
                    themeColor = LightDebt,
                    title = "ยังไม่มีหนี้สินหรือรายจ่าย",
                    description = "เพิ่มหนี้สินหรือรายจ่ายระยะยาวเพื่อดูภาระทางการเงินในภาพรวม",
                    actionLabel = "เพิ่มรายการ",
                    onAction = onAddClick,
                )
            } else if (filteredLoans.isEmpty() && filteredExpenses.isEmpty()) {
                FinancialListNoResultsState(
                    themeColor = LightDebt,
                    query = searchQuery,
                    onClear = { searchQuery = "" },
                )
            } else {
                LazyColumn {
                if (filteredLoans.isNotEmpty()) {
                    item {
                        ExpandableCategoryCard(title = "หนี้สิน", itemCount = filteredLoans.size, themeColor = "debt", initiallyExpanded = true) {
                            filteredLoans.forEach { loan ->
                                RealItemCard(
                                    title = loan.name ?: "",
                                    subtitleLabel = "เจ้าหนี้", subtitleValue = loan.creditor ?: "-",
                                    amountLabel = "ยอดหนี้", amountValue = "${formatAmount(loan.principal ?: Money(0))} บาท",
                                    onClick = { selectedLiabilityId = loan.id }
                                )
                            }
                        }
                    }
                }

                if (filteredExpenses.isNotEmpty()) {
                    item {
                        ExpandableCategoryCard(title = "รายจ่ายระยะยาว", itemCount = filteredExpenses.size, themeColor = "debt", initiallyExpanded = true) {
                            filteredExpenses.forEach { exp ->
                                RealItemCard(
                                    title = exp.name ?: "",
                                    subtitleLabel = "จ่ายให้",
                                    subtitleValue = exp.creditor?.takeIf { it.isNotBlank() } ?: "-",
                                    amountLabel = "ยอดชำระ", amountValue = "${formatAmount(exp.principal ?: Money(0))} บาท",
                                    onClick = { selectedLiabilityId = exp.id }
                                )
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(140.dp)) }
                }
            }
        }

        Surface(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 40.dp, end = 25.dp)
                .size(50.dp)
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

    // 🌟 Popup ยืนยันการลบ
    if (showConfirmDelete) {
        val annotatedMessage = buildAnnotatedString {
            append("คุณแน่ใจหรือไม่ว่าต้องการลบ ")
            withStyle(style = SpanStyle(color = LightDebt, fontWeight = FontWeight.Bold)) {
                append("'$itemNameToDelete'")
            }
            append(" ออกจากระบบ?")
        }

        ConfirmDeleteDialog(
            title = "ลบรายการ",
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

    // 🌟 เรียกใช้ Smart Dialog กลางแทน
    if (selectedLiabilityId != null && !showConfirmDelete) {
        SmartAssetDetailDialog(
            assetId = selectedLiabilityId!!,
            assetType = "liability", // ส่ง type เป็น liability
            showBottomMenu = true,
            onDismiss = {
                selectedLiabilityId = null
            },
            onDelete = { itemName ->
                itemNameToDelete = itemName
                showConfirmDelete = true
            },
            onShare = {
                navigatorContent.push(ShareAssetScreen("liability", selectedLiabilityId!!))
            },
            onEdit = { rawData ->
                if (rawData is LiabilityIdData) {
                    val isLoan = rawData.type == "LIABILITY_TYPE_LOAN"
                    val attachments = rawData.files?.map { it.toAttachment() } ?: emptyList()

                    if (isLoan) {
                        // 🌟 ส่งข้อมูลทั้งหมดไปให้ครบถ้วน สำหรับ "หนี้สิน"
                        val dataToSend = LiabilityModel(
                            type = rawData.type ?: "",
                            name = rawData.name ?: "",
                            creditor = rawData.creditor ?: "",
                            principal = rawData.principal ?: Money(0),
                            interestRate = rawData.interestRate?.decimalString() ?: "",
                            description = rawData.description ?: "",
                            attachments = attachments,
                            startedAt = rawData.startedAt ?: "",
                            endedAt = rawData.endedAt ?: ""
                        )
                        navigatorContent.push(LiabilityFormScreen(rawData.id, dataToSend))
                    } else {
                        // 🌟 ส่งข้อมูลทั้งหมดไปให้ครบถ้วน สำหรับ "ค่าใช้จ่าย" (เอาค่าเก่ามาถมไว้ก่อน เผื่อมีการแก้ Data Class อนาคต)
                        val dataToSend = ExpenseModel(
                            type = rawData.type ?: "",
                            name = rawData.name ?: "",
                            creditor = rawData.creditor ?: "",
                            principal = rawData.principal ?: Money(0),
                            interestRate = rawData.interestRate?.decimalString() ?: "",
                            description = rawData.description ?: "",
                            attachments = attachments,
                            startedAt = rawData.startedAt ?: "",
                            endedAt = rawData.endedAt ?: ""
                        )
                        navigatorContent.push(ExpenseFormScreen(rawData.id, dataToSend))
                    }
                }
            }
        )
    }
}

@Composable
private fun DebtLoadError(message: String, onRetry: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(horizontal = 24.dp),
        ) {
            Text(message, textAlign = TextAlign.Center, color = LightDebt)
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = LightDebt),
            ) {
                Text("ลองใหม่", color = LightSoftWhite)
            }
        }
    }
}

private fun debtErrorMessage(error: AppError): String = when (error) {
    AppError.Unauthorized -> "เซสชันหมดอายุ กรุณาเข้าสู่ระบบใหม่"
    AppError.NotFound -> "ไม่พบรายการหนี้สิน"
    is AppError.Network -> "เชื่อมต่อเซิร์ฟเวอร์ไม่ได้ กรุณาลองใหม่"
    is AppError.Unknown -> "โหลดหนี้สินไม่สำเร็จ กรุณาลองใหม่"
}
