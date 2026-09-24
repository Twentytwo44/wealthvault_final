package com.wealthvault.financiallist.ui.asset

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.wealthvault.core.architecture.AppError
import com.wealthvault.core.model.Money
import com.wealthvault.core.theme.LightAsset
import com.wealthvault.core.theme.LightSoftWhite
import com.wealthvault.core.utils.formatAmount
import com.wealthvault.domain.portfolio.AccountData
import com.wealthvault.domain.portfolio.GetBuildingData
import com.wealthvault.domain.portfolio.GetCashData
import com.wealthvault.domain.portfolio.GetInsuranceData
import com.wealthvault.domain.portfolio.GetInvestmentData
import com.wealthvault.domain.portfolio.GetLandData
import com.wealthvault.financiallist.ui.FinancialListEmptyState
import com.wealthvault.financiallist.ui.FinancialListNoResultsState
import com.wealthvault.financiallist.ui.component.ExpandableCategoryCard
import com.wealthvault.financiallist.ui.component.RealItemCard

@Composable
internal fun AssetListContent(
    searchQuery: String,
    isLoading: Boolean,
    error: AppError?,
    accounts: List<AccountData>,
    cashes: List<GetCashData>,
    investments: List<GetInvestmentData>,
    insurances: List<GetInsuranceData>,
    buildings: List<GetBuildingData>,
    lands: List<GetLandData>,
    onAddClick: () -> Unit,
    onRetry: () -> Unit,
    onClearSearch: () -> Unit,
    onAssetSelected: (id: String, type: String) -> Unit,
) {
    val filteredAccounts = remember(accounts, searchQuery) {
        accounts.filter { (it.name ?: "").contains(searchQuery, ignoreCase = true) }
    }
    val filteredCashes = remember(cashes, searchQuery) {
        cashes.filter { (it.name ?: "").contains(searchQuery, ignoreCase = true) }
    }
    val filteredInvestments = remember(investments, searchQuery) {
        investments.filter { (it.name ?: "").contains(searchQuery, ignoreCase = true) }
    }
    val filteredInsurances = remember(insurances, searchQuery) {
        insurances.filter { (it.name ?: "").contains(searchQuery, ignoreCase = true) }
    }
    val filteredBuildings = remember(buildings, searchQuery) {
        buildings.filter { (it.name ?: "").contains(searchQuery, ignoreCase = true) }
    }
    val filteredLands = remember(lands, searchQuery) {
        lands.filter { (it.name ?: "").contains(searchQuery, ignoreCase = true) }
    }

    val hasData = accounts.isNotEmpty() || cashes.isNotEmpty() ||
        investments.isNotEmpty() || insurances.isNotEmpty() ||
        buildings.isNotEmpty() || lands.isNotEmpty()

    when {
        isLoading && !hasData -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = LightAsset)
            }
        }

        !hasData && error != null -> {
            AssetLoadError(message = assetErrorMessage(error), onRetry = onRetry)
        }

        !hasData -> {
            FinancialListEmptyState(
                themeColor = LightAsset,
                title = "ยังไม่มีทรัพย์สิน",
                description = "เพิ่มบัญชี เงินสด การลงทุน ประกัน บ้าน หรือที่ดิน เพื่อเริ่มติดตามภาพรวมการเงิน",
                actionLabel = "เพิ่มทรัพย์สิน",
                onAction = onAddClick,
            )
        }

        filteredAccounts.isEmpty() &&
            filteredCashes.isEmpty() &&
            filteredInvestments.isEmpty() &&
            filteredInsurances.isEmpty() &&
            filteredBuildings.isEmpty() &&
            filteredLands.isEmpty() -> {
            FinancialListNoResultsState(
                themeColor = LightAsset,
                query = searchQuery,
                onClear = onClearSearch,
            )
        }

        else -> {
            LazyColumn {
                if (filteredAccounts.isNotEmpty()) {
                    item {
                        ExpandableCategoryCard(
                            title = "บัญชีเงินฝาก",
                            itemCount = filteredAccounts.size,
                            themeColor = "asset",
                            initiallyExpanded = true,
                        ) {
                            filteredAccounts.forEach { account ->
                                RealItemCard(
                                    title = account.name ?: "",
                                    subtitleLabel = "ธนาคาร",
                                    subtitleValue = account.bankName ?: "",
                                    amountLabel = "ยอดเงิน",
                                    amountValue = "${formatAmount(account.amount ?: Money(0))} บาท",
                                    onClick = { onAssetSelected(account.id, "account") },
                                )
                            }
                        }
                    }
                }

                if (filteredCashes.isNotEmpty()) {
                    item {
                        ExpandableCategoryCard(
                            title = "เงินสด ทองคำ",
                            itemCount = filteredCashes.size,
                            themeColor = "asset",
                        ) {
                            filteredCashes.forEach { cash ->
                                RealItemCard(
                                    title = cash.name ?: "",
                                    subtitleLabel = "รายละเอียด",
                                    subtitleValue = cash.description?.takeIf { it.isNotBlank() } ?: "-",
                                    amountLabel = "มูลค่า",
                                    amountValue = "${formatAmount(cash.ammount ?: Money(0))} บาท",
                                    onClick = { cash.id?.let { onAssetSelected(it, "cash") } },
                                )
                            }
                        }
                    }
                }

                if (filteredInvestments.isNotEmpty()) {
                    item {
                        ExpandableCategoryCard(
                            title = "ลงทุน หุ้น กองทุน",
                            itemCount = filteredInvestments.size,
                            themeColor = "asset",
                        ) {
                            filteredInvestments.forEach { invest ->
                                RealItemCard(
                                    title = invest.name ?: "ไม่ระบุชื่อ",
                                    subtitleLabel = "โบรกเกอร์",
                                    subtitleValue = invest.brokerName ?: "",
                                    amountLabel = "มูลค่ารวม",
                                    amountValue = "${formatAmount(invest.amount ?: Money(0))} บาท",
                                    onClick = { invest.id?.let { onAssetSelected(it, "investment") } },
                                )
                            }
                        }
                    }
                }

                if (filteredInsurances.isNotEmpty()) {
                    item {
                        ExpandableCategoryCard(
                            title = "ประกัน",
                            itemCount = filteredInsurances.size,
                            themeColor = "asset",
                        ) {
                            filteredInsurances.forEach { insurance ->
                                RealItemCard(
                                    title = insurance.name ?: "",
                                    subtitleLabel = "บริษัท",
                                    subtitleValue = insurance.companyName ?: "",
                                    amountLabel = "วงเงินคุ้มครอง",
                                    amountValue = "${formatAmount(insurance.coverageAmount ?: Money(0))} บาท",
                                    onClick = { insurance.id?.let { onAssetSelected(it, "insurance") } },
                                )
                            }
                        }
                    }
                }

                if (filteredBuildings.isNotEmpty()) {
                    item {
                        ExpandableCategoryCard(
                            title = "บ้าน ตึก อาคาร",
                            itemCount = filteredBuildings.size,
                            themeColor = "asset",
                        ) {
                            filteredBuildings.forEach { building ->
                                RealItemCard(
                                    title = building.name ?: "",
                                    subtitleLabel = "พื้นที่",
                                    subtitleValue = "${formatAmount(building.area ?: 0.0)} ตร.ม.",
                                    amountLabel = "มูลค่าประเมิน",
                                    amountValue = "${formatAmount(building.amount ?: Money(0))} บาท",
                                    onClick = { building.id?.let { onAssetSelected(it, "building") } },
                                )
                            }
                        }
                    }
                }

                if (filteredLands.isNotEmpty()) {
                    item {
                        ExpandableCategoryCard(
                            title = "ที่ดิน",
                            itemCount = filteredLands.size,
                            themeColor = "asset",
                        ) {
                            filteredLands.forEach { land ->
                                RealItemCard(
                                    title = land.name ?: "",
                                    subtitleLabel = "เลขโฉนด",
                                    subtitleValue = land.deedNum ?: "",
                                    amountLabel = "มูลค่าประเมิน",
                                    amountValue = "${formatAmount(land.amount ?: Money(0))} บาท",
                                    onClick = { land.id?.let { onAssetSelected(it, "land") } },
                                )
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(140.dp)) }
            }
        }
    }
}

@Composable
private fun AssetLoadError(message: String, onRetry: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(horizontal = 24.dp),
        ) {
            Text(message, textAlign = TextAlign.Center, color = LightAsset)
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = LightAsset),
            ) {
                Text("ลองใหม่", color = LightSoftWhite)
            }
        }
    }
}

private fun assetErrorMessage(error: AppError): String = when (error) {
    AppError.Unauthorized -> "เซสชันหมดอายุ กรุณาเข้าสู่ระบบใหม่"
    AppError.NotFound -> "ไม่พบรายการทรัพย์สิน"
    is AppError.Network -> "เชื่อมต่อเซิร์ฟเวอร์ไม่ได้ กรุณาลองใหม่"
    is AppError.Unknown -> "โหลดทรัพย์สินไม่สำเร็จ กรุณาลองใหม่"
}
