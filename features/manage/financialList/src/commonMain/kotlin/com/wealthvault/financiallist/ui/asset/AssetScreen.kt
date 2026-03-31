package com.wealthvault.financiallist.ui.asset

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
import com.wealthvault.core.generated.resources.ic_nav_asset
import com.wealthvault.core.theme.LightAsset
import com.wealthvault.core.utils.getScreenModel
import com.wealthvault.financiallist.ui.FinancialListTemplate
import com.wealthvault.financiallist.ui.component.ExpandableCategoryCard
import com.wealthvault.financiallist.ui.component.RealItemCard
import com.wealthvault.financiallist.ui.component.DetailDialog // 🌟 อย่าลืม import Dialog ที่เราสร้าง
import com.wealthvault.financiallist.ui.component.DetailRow    // 🌟 อย่าลืม import Row ที่เราสร้าง
import org.jetbrains.compose.resources.painterResource

// 🌟 Import Data Class
import com.wealthvault.account_api.model.AccountData
import com.wealthvault.cash_api.model.GetCashData
import com.wealthvault.investment_api.model.GetInvestmentData
import com.wealthvault.insurance_api.model.GetInsuranceData
import com.wealthvault.building_api.model.GetBuildingData
import com.wealthvault.land_api.model.GetLandData
import kotlin.math.roundToInt

class AssetScreen(private val onAddClick: () -> Unit) : Screen {
    @Composable
    override fun Content() {
        val screenModel = getScreenModel<AssetScreenModel>()

        LaunchedEffect(Unit) {
            screenModel.fetchAllAssets()
        }

        val accounts by screenModel.accounts.collectAsState()
        val cashes by screenModel.cashes.collectAsState()
        val investments by screenModel.investments.collectAsState()
        val insurances by screenModel.insurances.collectAsState()
        val buildings by screenModel.buildings.collectAsState()
        val lands by screenModel.lands.collectAsState()

        AssetContent(
            onAddClick = onAddClick,
            accounts = accounts,
            cashes = cashes,
            investments = investments,
            insurances = insurances,
            buildings = buildings,
            lands = lands
        )
    }
}

@Composable
fun AssetContent(
    onAddClick: () -> Unit,
    accounts: List<AccountData>,
    cashes: List<GetCashData>,
    investments: List<GetInvestmentData>,
    insurances: List<GetInsuranceData>,
    buildings: List<GetBuildingData>,
    lands: List<GetLandData>
) {
    var searchQuery by remember { mutableStateOf("") }
    // 🌟 สร้าง State สำหรับเก็บข้อมูลที่จะโชว์ใน Popup
    var selectedItem by remember { mutableStateOf<Any?>(null) }

    val filteredAccounts = accounts.filter { it.name.contains(searchQuery, ignoreCase = true) }
    val filteredCashes = cashes.filter { it.name.contains(searchQuery, ignoreCase = true) }
    val filteredInvestments = investments.filter { it.name.contains(searchQuery, ignoreCase = true) }
    val filteredInsurances = insurances.filter { it.name.contains(searchQuery, ignoreCase = true) }
    val filteredBuildings = buildings.filter { it.name.contains(searchQuery, ignoreCase = true) }
    val filteredLands = lands.filter { it.name.contains(searchQuery, ignoreCase = true) }

    FinancialListTemplate(
        headerTitle = "ทรัพย์สิน",
        themeColor = LightAsset,
        searchQuery = searchQuery,
        onSearchChange = { searchQuery = it },
        onAddClick = onAddClick,
        headerIcon = {
            Icon(
                painter = painterResource(Res.drawable.ic_nav_asset),
                contentDescription = null,
                tint = LightAsset,
                modifier = Modifier.padding(horizontal = 4.dp)
                    .size(28.dp)
            )
        }
    ) {
        LazyColumn {
            // 1. บัญชีเงินฝาก
            if (filteredAccounts.isNotEmpty()) {
                item {
                    ExpandableCategoryCard(title = "บัญชีเงินฝาก", itemCount = filteredAccounts.size, themeColor = "asset", initiallyExpanded = true) {
                        filteredAccounts.forEach { account ->
                            RealItemCard(
                                title = account.name,
                                subtitleLabel = "ธนาคาร", subtitleValue = account.bankName,
                                amountLabel = "ยอดเงิน", amountValue = "${account.amount} บาท",
                                onClick = { selectedItem = account } // 🌟 เพิ่ม onClick
                            )
                        }
                    }
                }
            }

            // 2. เงินสด ทองคำ
            if (filteredCashes.isNotEmpty()) {
                item {
                    ExpandableCategoryCard(title = "เงินสด ทองคำ", itemCount = filteredCashes.size, themeColor = "asset") {
                        filteredCashes.forEach { cash ->
                            RealItemCard(
                                title = cash.name,
                                subtitleLabel = "รายละเอียด", subtitleValue = cash.description.ifEmpty { "เงินสด" },
                                amountLabel = "มูลค่า", amountValue = "${cash.amount} บาท",
                                onClick = { selectedItem = cash } // 🌟 เพิ่ม onClick
                            )
                        }
                    }
                }
            }

            // 3. ลงทุน หุ้น กองทุน
            if (filteredInvestments.isNotEmpty()) {
                item {
                    ExpandableCategoryCard(title = "ลงทุน หุ้น กองทุน", itemCount = filteredInvestments.size, themeColor = "asset") {
                        filteredInvestments.forEach { invest ->
                            val rawTotal = invest.quantity * invest.costPerPrice
                            val totalVal = (rawTotal * 100.0).roundToInt() / 100.0
                            RealItemCard(
                                title = "${invest.name} (${invest.symbol})",
                                subtitleLabel = "โบรกเกอร์", subtitleValue = invest.brokerName,
                                amountLabel = "มูลค่ารวม", amountValue = "${totalVal} บาท",
                                onClick = { selectedItem = invest } // 🌟 เพิ่ม onClick
                            )
                        }
                    }
                }
            }

            // 4. ประกัน
            if (filteredInsurances.isNotEmpty()) {
                item {
                    ExpandableCategoryCard(title = "ประกัน", itemCount = filteredInsurances.size, themeColor = "asset") {
                        filteredInsurances.forEach { insurance ->
                            RealItemCard(
                                title = insurance.name,
                                subtitleLabel = "บริษัท", subtitleValue = insurance.companyName,
                                amountLabel = "วงเงินคุ้มครอง", amountValue = "${insurance.coverageAmount} บาท",
                                onClick = { selectedItem = insurance } // 🌟 เพิ่ม onClick
                            )
                        }
                    }
                }
            }

            // 5. บ้าน ตึก อาคาร
            if (filteredBuildings.isNotEmpty()) {
                item {
                    ExpandableCategoryCard(title = "บ้าน ตึก อาคาร", itemCount = filteredBuildings.size, themeColor = "asset") {
                        filteredBuildings.forEach { building ->
                            RealItemCard(
                                title = building.name,
                                subtitleLabel = "พื้นที่", subtitleValue = "${building.area} ตร.ม.",
                                amountLabel = "มูลค่าประเมิน", amountValue = "${building.amount} บาท",
                                onClick = { selectedItem = building } // 🌟 เพิ่ม onClick
                            )
                        }
                    }
                }
            }

            // 6. ที่ดิน
            if (filteredLands.isNotEmpty()) {
                item {
                    ExpandableCategoryCard(title = "ที่ดิน", itemCount = filteredLands.size, themeColor = "asset") {
                        filteredLands.forEach { land ->
                            RealItemCard(
                                title = land.name,
                                subtitleLabel = "เลขโฉนด", subtitleValue = land.deedNum,
                                amountLabel = "มูลค่าประเมิน", amountValue = "${land.amount} บาท",
                                onClick = { selectedItem = land } // 🌟 เพิ่ม onClick
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
        when (item) {
            is AccountData -> {
                DetailDialog(
                    subtitle = "ทรัพย์สิน · บัญชีเงินฝาก",
                    title = item.name,
                    themeType = "asset",
                    onDismiss = { selectedItem = null }
                ) {
                    DetailRow("ธนาคาร", item.bankName)
                    DetailRow("เลขบัญชี", item.bankAccount)
                    DetailRow("ประเภท", item.type)
                    DetailRow("ยอดเงิน", "${item.amount} บาท") // 🌟
                    DetailRow("คำอธิบาย", item.description, isLast = true)
                }
            }
            is GetCashData -> {
                DetailDialog(
                    subtitle = "ทรัพย์สิน · เงินสด ทองคำ",
                    title = item.name,
                    themeType = "asset",
                    onDismiss = { selectedItem = null }
                ) {
                    DetailRow("มูลค่า", "${item.amount} บาท")  // 🌟
                    DetailRow("คำอธิบาย", item.description, isLast = true)
                }
            }
            is GetInvestmentData -> {
                DetailDialog(
                    subtitle = "ทรัพย์สิน · ลงทุน หุ้น กองทุน",
                    title = "${item.name} (${item.symbol})",
                    themeType = "asset",
                    onDismiss = { selectedItem = null }
                ) {
                    DetailRow("โบรกเกอร์", item.brokerName)
                    DetailRow("จำนวน", "${item.quantity}")
                    DetailRow("ราคาทุนต่อหน่วย", "${item.costPerPrice} บาท") // 🌟
                    DetailRow("ประเภท", item.type)
                    DetailRow("คำอธิบาย", item.description, isLast = true)
                }
            }
            is GetInsuranceData -> {
                DetailDialog(
                    subtitle = "ทรัพย์สิน · ประกัน",
                    title = item.name,
                    themeType = "asset",
                    onDismiss = { selectedItem = null }
                ) {
                    DetailRow("เลขกรมธรรม์", item.policyNumber)
                    DetailRow("บริษัท", item.companyName)
                    DetailRow("วงเงินคุ้มครอง", "${item.coverageAmount} บาท") // 🌟
                    DetailRow("ระยะเวลาคุ้มครอง", "${item.coveragePeriod} ปี")
                    DetailRow("วันเริ่มสัญญา", item.conDate.take(10))
                    DetailRow("วันสิ้นสุดสัญญา", item.expDate.take(10), isLast = true)
                }
            }
            is GetBuildingData -> {
                DetailDialog(
                    subtitle = "ทรัพย์สิน · บ้าน ตึก อาคาร",
                    title = item.name,
                    themeType = "asset",
                    onDismiss = { selectedItem = null }
                ) {
                    DetailRow("ประเภท", item.type)
                    DetailRow("พื้นที่", "${item.area} ตร.ม.")
                    DetailRow("มูลค่าประเมิน", "${item.amount} บาท") // 🌟
                    DetailRow("ที่อยู่", "${item.location.address} ${item.location.district} ${item.location.province}")
                    DetailRow("คำอธิบาย", item.description, isLast = true)
                }
            }
            is GetLandData -> {
                DetailDialog(
                    subtitle = "ทรัพย์สิน · ที่ดิน",
                    title = item.name,
                    themeType = "asset",
                    onDismiss = { selectedItem = null }
                ) {
                    DetailRow("เลขโฉนด", item.deedNum)
                    DetailRow("ขนาดพื้นที่", "${item.area} ตารางวา")
                    DetailRow("มูลค่าประเมิน", "${item.amount} บาท") // 🌟
                    DetailRow("จังหวัด", item.location.province)
                    DetailRow("คำอธิบาย", item.description, isLast = true)
                }
            }
        }
    }
}